package com.newaura.bookish.features.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.core.data.ButtonState
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.post.data.CreatePostRequest
import com.newaura.bookish.features.post.data.ImageFile
import com.newaura.bookish.features.post.data.PostData
import com.newaura.bookish.features.post.domain.CreatePostUseCase
import com.newaura.bookish.features.post.ui.CreatePostScreenState
import com.newaura.bookish.features.post.ui.CreatePostUiState
import com.newaura.bookish.features.search.ui.SearchBooksViewModel
import com.newaura.bookish.model.BookDetail
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class CreatePostViewModel(
    private val createPostUseCase: CreatePostUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _postScreenState = MutableStateFlow(CreatePostScreenState())
    val postScreenState: StateFlow<CreatePostScreenState> = _postScreenState.asStateFlow()

    private val _postUiDataState = MutableStateFlow<CreatePostUiState>(CreatePostUiState.Idle)
    val postUiDataState: StateFlow<CreatePostUiState> = _postUiDataState.asStateFlow()

    private val _postButtonState = MutableStateFlow<ButtonState>(ButtonState.Disabled)
    val postButtonState: StateFlow<ButtonState> = _postButtonState.asStateFlow()

    private var bookSelectionJob: Job? = null

    init {
        listenToSelectedBook()
    }

    private fun listenToSelectedBook() {
        bookSelectionJob = viewModelScope.launch {
            try {
                val searchViewModel = KoinJavaComponent.get<SearchBooksViewModel>(SearchBooksViewModel::class.java)
                searchViewModel.screenState.collect { searchState ->
                    if (searchState.selectedBook != null) {
                        _postScreenState.update { currentState ->
                            currentState.copy(selectedBook = searchState.selectedBook)
                        }
                    }
                }
            } catch (e: Exception) {
                // SearchBooksViewModel might not be available, that's fine
            }
        }
    }

    fun updateUiState(postScreenState: CreatePostScreenState) {
        _postScreenState.value = postScreenState
    }

    fun createPost() {
        val userId = userDataStore.currentUserId
        if (userId.isNullOrEmpty()) {
            _postUiDataState.value = CreatePostUiState.Error("User not authenticated")
            return
        }

        if (_postScreenState.value.selectedBook == null) {
            _postUiDataState.value = CreatePostUiState.Error("Please select a book")
            return
        }

        if (_postScreenState.value.postCaption.isEmpty()) {
            _postUiDataState.value = CreatePostUiState.Error("Please add your thoughts")
            return
        }

        viewModelScope.launch {
            _postUiDataState.value = CreatePostUiState.Loading

            try {
                val uploadedImageUrls = _postScreenState.value.selectedImages.map { imageFile ->
                    uploadImageFile(imageFile)
                }

                val bookCategories =
                    _postScreenState.value.selectedBook?.volumeInfo?.categories?.map { it }
                        ?: emptyList()

                val requestBody = CreatePostRequest(
                    userId = userId,
                    isLiked = false,
                    post = PostData(
                        caption = _postScreenState.value.postCaption,
                        images = uploadedImageUrls,
                        bookName = _postScreenState.value.selectedBook?.volumeInfo?.title ?: "",
                        bookLink = _postScreenState.value.bookLink,
                        bookId = _postScreenState.value.selectedBook?.id ?: "",
                        bookCategories = bookCategories,
                        postType = _postScreenState.value.selectedPostType.name.lowercase()
                    )
                )

                createPostUseCase.invoke(requestBody).collect { result ->
                    result.onSuccess { response ->
                        _postUiDataState.value =
                            CreatePostUiState.Success("Post created successfully")
                        clearForm()
                    }
                    result.onFailure { exception ->
                        handleError(exception)
                    }
                }
            } catch (exception: Exception) {
                handleError(exception)
            }
        }
    }

    private fun uploadImageFile(imageFile: ImageFile): String {
        return try {
            // TODO: Implement Firebase Storage upload
            imageFile.path
        } catch (exception: Exception) {
            throw Exception("Error uploading image: ${exception.message}")
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

        _postUiDataState.value = CreatePostUiState.Error(errorMessage)
    }

    private fun clearForm() {
        _postScreenState.update {
            CreatePostScreenState()
        }
    }

    override fun onCleared() {
        bookSelectionJob?.cancel()
        super.onCleared()
    }
}

