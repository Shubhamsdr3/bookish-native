package com.newaura.bookish.features.feed.ui

sealed interface ButtonState {
    data object Loading: ButtonState
    data object Disabled: ButtonState
    data object Enabled: ButtonState
}

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object OtpSent : LoginUiState
    data class Success(val userId: String) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

data class LoginScreenState(
    val uiState: LoginUiState = LoginUiState.Idle,
    val phoneNumber: String = "",
    val otp: String = "",
    val isOtpScreen: Boolean = false
)