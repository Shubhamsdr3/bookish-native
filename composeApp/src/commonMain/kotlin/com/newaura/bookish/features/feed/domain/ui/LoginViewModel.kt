package com.newaura.bookish.features.feed.domain.ui

import com.newaura.bookish.features.feed.domain.SendOtpUseCase
import com.newaura.bookish.features.feed.domain.SignInWithGoogleUseCase
import com.newaura.bookish.features.feed.domain.VerifyOtpUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx. coroutines.Dispatchers
import kotlinx.coroutines. SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx. coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    coroutineScope: CoroutineScope? = null
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _screenState = MutableStateFlow(LoginScreenState())
    val screenState: StateFlow<LoginScreenState> = _screenState.asStateFlow()

    fun updatePhoneNumber(phone: String) {
        _screenState.update { it.copy(phoneNumber = phone) }
    }

    fun updateOtp(otp: String) {
        _screenState.update { it.copy(otp = otp) }
    }

    fun sendOtp() {
        val phoneNumber = _screenState.value.phoneNumber
        if (phoneNumber.isBlank()) return

        viewModelScope.launch {
            _screenState.update { it.copy(uiState = LoginUiState.Loading) }

            sendOtpUseCase(phoneNumber).fold(
                onSuccess = {
                    _screenState.update {
                        it.copy(
                            uiState = LoginUiState.OtpSent,
                            isOtpScreen = true
                        )
                    }
                },
                onFailure = { error ->
                    _screenState.update {
                        it.copy(uiState = LoginUiState. Error(error.message ?: "Failed to send OTP"))
                    }
                }
            )
        }
    }

    fun verifyOtp() {
        val state = _screenState.value
        if (state.otp.length < 5) return

        viewModelScope.launch {
            _screenState.update { it.copy(uiState = LoginUiState.Loading) }

            verifyOtpUseCase(state.phoneNumber, state.otp).fold(
                onSuccess = { userId ->
                    _screenState.update {
                        it.copy(uiState = LoginUiState. Success(userId))
                    }
                },
                onFailure = { error ->
                    _screenState.update {
                        it.copy(uiState = LoginUiState.Error(error.message ?: "Invalid OTP"))
                    }
                }
            )
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _screenState.update { it.copy(uiState = LoginUiState.Loading) }

            signInWithGoogleUseCase().fold(
                onSuccess = { userId ->
                    _screenState.update {
                        it. copy(uiState = LoginUiState.Success(userId))
                    }
                },
                onFailure = { error ->
                    _screenState.update {
                        it.copy(uiState = LoginUiState. Error(error.message ?: "Google sign-in failed"))
                    }
                }
            )
        }
    }

    fun goBack() {
        _screenState. update {
            it.copy(isOtpScreen = false, otp = "", uiState = LoginUiState. Idle)
        }
    }
}