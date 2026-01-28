package com.newaura.bookish.features.post.ui

sealed interface CreatePostUiState {
    data object Idle : CreatePostUiState
    data object Loading : CreatePostUiState
    data object OtpSent : CreatePostUiState
    data class Success(val userId: String) : CreatePostUiState
    data class Error(val message: String) : CreatePostUiState
}

data class CreatePostScreenState(
    val uiState: CreatePostUiState = CreatePostUiState.Idle,
    val phoneNumber: String = "",
    val otp: String = "",
    val isOtpScreen: Boolean = false
)