package com.newaura.bookish.features.post.ui

import com.newaura.bookish.features.post.data.dto.ImageFile
import com.newaura.bookish.model.BookDetail
import com.newaura.bookish.model.PostType

sealed interface CreatePostUiState {
    data object Idle : CreatePostUiState
    data object Loading : CreatePostUiState
    data class Success(val message: String) : CreatePostUiState
    data class Error(val message: String) : CreatePostUiState
    data object NavigateToSearch : CreatePostUiState
    data object NavigateToHome : CreatePostUiState

}

data class CreatePostScreenState(
    val uiState: CreatePostUiState = CreatePostUiState.Idle,
    val postCaption: String = "",
    val bookTitle: String = "",
    val bookLink: String = "",
    val selectedPostType: PostType = PostType.THOUGHT,
    val selectedImages: List<ImageFile> = emptyList(),
    val selectedBook: BookDetail? = null,
)