package com.newaura.bookish.features.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.features.post.domain.CreatePostUseCase
import com.newaura.bookish.features.post.ui.CreatePostScreenState
import com.newaura.bookish.features.post.ui.CreatePostUiState
import com.newaura.bookish.model.BookDetail
import com.newaura.bookish.model.PostType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ImageFile(
    val name: String,
    val path: String,
    val byteArray: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageFile) return false
        return name == other.name && path == other.path
    }

    override fun hashCode(): Int {
        return name.hashCode() + path.hashCode()
    }
}

enum class ButtonState {
    ENABLED, DISABLED
}

data class CreatePostRequest(
    val userId: String,
    val isLiked: Boolean,
    val post: PostPayload
)

data class PostPayload(
    val caption: String,
    val images: List<String>,
    val bookName: String,
    val bookLink: String,
    val bookId: String,
    val bookCategories: List<String>,
    val postType: String = "review"
)

class CreatePostViewModel(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _postScreenState = MutableStateFlow(CreatePostScreenState())
    val postScreenState: StateFlow<CreatePostScreenState> = _postScreenState.asStateFlow()

    private val _postCaption = MutableStateFlow("")
    val postCaption: StateFlow<String> = _postCaption.asStateFlow()

    private val _bookTitle = MutableStateFlow("")
    val bookTitle: StateFlow<String> = _bookTitle.asStateFlow()

    private val _bookLink = MutableStateFlow("")
    val bookLink: StateFlow<String> = _bookLink.asStateFlow()

    private val _selectedImages = MutableStateFlow<List<ImageFile>>(emptyList())
    val selectedImages: StateFlow<List<ImageFile>> = _selectedImages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _buttonState = MutableStateFlow(ButtonState.DISABLED)
    val buttonState: StateFlow<ButtonState> = _buttonState.asStateFlow()

    private val _selectedBook = MutableStateFlow<BookDetail?>(null)
    val selectedBook: StateFlow<BookDetail?> = _selectedBook.asStateFlow()

    private val _selectedPostType = MutableStateFlow(PostType.REVIEW)
    val selectedPostType: StateFlow<PostType> = _selectedPostType.asStateFlow()

    fun updatePostCaption(caption: String) {
        _postCaption.update { caption }
        enablePostButton()
    }

    fun updateBookTitle(title: String) {
        _bookTitle.update { title }
        enablePostButton()
    }

    fun updateBookLink(link: String) {
        _bookLink.update { link }
    }

    fun updatePostType(postType: PostType) {
        _selectedPostType.update { postType }
    }

    fun addImages(images: List<ImageFile>) {
        _selectedImages.update { currentImages -> currentImages + images }
    }

    fun removeImage(index: Int) {
        _selectedImages.update { currentImages ->
            currentImages.filterIndexed { i, _ -> i != index }
        }
    }

    fun clearImages() {
        _selectedImages.update { emptyList() }
    }

    fun setSelectedBook(bookDetail: BookDetail) {
        _selectedBook.update { bookDetail }
        _bookTitle.update { bookDetail.volumeInfo?.title ?: "" }
    }

    fun navigateToSearchSuggestion(onResult: (BookDetail?) -> Unit) {
        _postScreenState.update {
            it.copy(uiState = CreatePostUiState.NavigateToSearch)
        }
    }

    fun onGalleryImageSelected(imageFiles: List<ImageFile>) {
        addImages(imageFiles)
        updateSelectedImageCount()
    }

    fun onCameraImageCaptured(imageFile: ImageFile) {
        addImages(listOf(imageFile))
        updateSelectedImageCount()
    }

    fun onVideoSelected(videoFile: ImageFile) {
        addImages(listOf(videoFile))
        updateSelectedImageCount()
    }

    private fun updateSelectedImageCount() {
        _postScreenState.update {
            it.copy(selectedImageCount = _selectedImages.value.size)
        }
    }

    fun navigateToHome() {
        _postScreenState.update {
            it.copy(uiState = CreatePostUiState.NavigateToHome)
        }
    }

    fun createPost() {
        if (_buttonState.value == ButtonState.DISABLED) return

        viewModelScope.launch {
            _isLoading.update { true }
            _postScreenState.update { it.copy(uiState = CreatePostUiState.Loading) }

            try {
                val uploadedImageUrls = _selectedImages.value.map { imageFile ->
                    uploadImageFile(imageFile)
                }

                val userId = getUserId()
                val bookCategories = _selectedBook.value?.volumeInfo?.categories?.map { it } ?: emptyList()

                val requestBody = CreatePostRequest(
                    userId = userId,
                    isLiked = false,
                    post = PostPayload(
                        caption = _postCaption.value,
                        images = uploadedImageUrls,
                        bookName = _bookTitle.value,
                        bookLink = _bookLink.value,
                        bookId = _selectedBook.value?.id ?: "",
                        bookCategories = bookCategories,
                        postType = _selectedPostType.value.name.lowercase()
                    )
                )

                createPostUseCase.invoke(
                    caption = requestBody.post.caption,
                    images = requestBody.post.images
                ).collect { result ->
                    result.onSuccess {
                        _isLoading.update { false }
                        _postScreenState.update {
                            it.copy(uiState = CreatePostUiState.Success("Post created successfully"))
                        }
                        clearForm()
                    }
                    result.onFailure { exception ->
                        _isLoading.update { false }
                        handleError(exception)
                    }
                }
            } catch (exception: Exception) {
                _isLoading.update { false }
                handleError(exception)
            }
        }
    }

    private suspend fun uploadImageFile(imageFile: ImageFile): String {
        return try {
            // TODO: Implement Firebase Storage upload
            imageFile.path
        } catch (exception: Exception) {
            throw Exception("Error uploading image: ${exception.message}")
        }
    }

    private suspend fun getUserId(): String {
        return try {
            // TODO: Implement actual UserStore.getUserId() for KMP
            ""
        } catch (exception: Exception) {
            throw Exception("Failed to get user ID: ${exception.message}")
        }
    }

    private fun handleError(exception: Throwable) {
        val errorMessage = when {
            exception.message?.contains("Unauthorized", ignoreCase = true) == true ->
                "Session expired. Please login again."
            exception.message?.contains("Network", ignoreCase = true) == true ->
                "Network error. Please check your connection."
            else -> exception.message ?: "Unknown error occurred"
        }

        _postScreenState.update {
            it.copy(uiState = CreatePostUiState.Error(errorMessage))
        }
    }

    private fun enablePostButton() {
        val isButtonEnabled = !_isLoading.value &&
            _bookTitle.value.isNotEmpty() &&
            _postCaption.value.isNotEmpty()

        _buttonState.update {
            if (isButtonEnabled) ButtonState.ENABLED else ButtonState.DISABLED
        }
    }

    private fun clearForm() {
        _postCaption.update { "" }
        _bookTitle.update { "" }
        _bookLink.update { "" }
        _selectedImages.update { emptyList() }
        _selectedBook.update { null }
        _selectedPostType.update { PostType.REVIEW }
        _buttonState.update { ButtonState.DISABLED }
    }

    override fun onCleared() {
        clearForm()
        super.onCleared()
    }
}

