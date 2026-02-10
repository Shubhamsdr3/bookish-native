package com.newaura.bookish.features.post.ui

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
    val bookTitle: String = "",
    val selectedPostType: PostType = PostType.REVIEW,
    val thoughts: String = "",
    val isSubmitting: Boolean = false,
    val selectedImageCount: Int = 0,
    val borderColor: String = "#CCCCCC", // Grey
    val linkBorderColor: String = "#CCCCCC",
    val searchInputBorderColor: String = "#CCCCCC"
)