package com.newaura.bookish.features.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.core.data.ButtonState
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.core.domain.UserState
import com.newaura.bookish.features.post.data.CreatePostRequest
import com.newaura.bookish.features.post.data.ImageFile
import com.newaura.bookish.features.post.data.PostData
import com.newaura.bookish.features.post.domain.CreatePostUseCase
import com.newaura.bookish.features.post.ui.CreatePostScreenState
import com.newaura.bookish.features.post.ui.CreatePostUiState
import com.newaura.bookish.model.BookDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatePostViewModel(
    private val createPostUseCase: CreatePostUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _postScreenState = MutableStateFlow(CreatePostScreenState())
    val postScreenState: StateFlow<CreatePostScreenState> = _postScreenState.asStateFlow()

    private val _postUiDataState = MutableStateFlow(CreatePostUiState.Idle)
    val postUiDataState: StateFlow<CreatePostUiState> = _postUiDataState.asStateFlow()

    private val _postButtonState = MutableStateFlow<ButtonState>(ButtonState.Disabled)
    val postButtonState: StateFlow<ButtonState> = _postButtonState.asStateFlow()

    fun navigateToSearchSuggestion(onResult: (BookDetail?) -> Unit) {
        _postScreenState.update {
            it.copy(uiState = CreatePostUiState.NavigateToSearch)
        }
    }

    fun navigateToHome() {
        _postScreenState.update {
            it.copy(uiState = CreatePostUiState.NavigateToHome)
        }
    }

    fun updateUiState(postScreenState: CreatePostScreenState) {
        _postScreenState.value = postScreenState
    }

    fun createPost() {
//        if (_postButtonState.value == ButtonState.Disabled) return

        val userId = userDataStore.currentUserId
        if(userId.isNullOrEmpty()) return

        viewModelScope.launch {
            _postScreenState.update { it.copy(uiState = CreatePostUiState.Loading) }

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
                        bookName = _postScreenState.value.bookTitle,
                        bookLink = _postScreenState.value.bookLink,
                        bookId = _postScreenState.value.selectedBook?.id ?: "",
                        bookCategories = bookCategories,
                        postType = _postScreenState.value.selectedPostType.name.lowercase()
                    )
                )

                createPostUseCase.invoke(requestBody).collect { result ->
                    result.onSuccess {
                        _postScreenState.update {
                            it.copy(uiState = CreatePostUiState.Success("Post created successfully"))
                        }
                        clearForm()
                    }
                    result.onFailure { exception ->
                        println(exception)
//                        _postScreenState.update {
//                            it.copy(
//                                uiState = CreatePostUiState.Error(
//                                    exception.message ?: "Something went wrong"
//                                )
//                            )
//                        }
//                        handleError(exception)
                    }
                }
            } catch (exception: Exception) {
                _postScreenState.update {
                    it.copy(
                        uiState = CreatePostUiState.Error(
                            exception.message ?: "Something went wrong"
                        )
                    )
                }
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

        _postScreenState.update {
            it.copy(uiState = CreatePostUiState.Error(errorMessage))
        }
    }

    private fun enablePostButton() {
        val currentState = _postScreenState.value
        val isButtonEnabled = currentState != CreatePostUiState.Loading &&
                currentState.bookTitle.isNotEmpty() &&
                currentState.postCaption.isNotEmpty()

        _postButtonState.value = if (isButtonEnabled) ButtonState.Enabled else ButtonState.Disabled
    }

    private fun clearForm() {
//        _postScreenState.update {
//            TODO("clear form")
//        }
    }

    override fun onCleared() {
        clearForm()
        super.onCleared()
    }
}

