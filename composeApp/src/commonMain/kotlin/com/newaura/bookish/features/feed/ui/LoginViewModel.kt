package com.newaura.bookish.features.feed.ui

import androidx.lifecycle.ViewModel
import com.newaura.bookish.core.ActivityContext
import com.newaura.bookish.core.data.ButtonState
import com.newaura.bookish.core.domain.AppDataStoreRepository
import com.newaura.bookish.core.domain.DataStoreKeys
import com.newaura.bookish.features.feed.domain.LoginUserUseCase
import com.newaura.bookish.features.feed.domain.SendOtpUseCase
import com.newaura.bookish.features.feed.domain.SignInWithGoogleUseCase
import com.newaura.bookish.features.feed.domain.VerifyOtpUseCase
import com.newaura.bookish.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class LoginViewModel(
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val appDataStoreRepository: AppDataStoreRepository,
    coroutineScope: CoroutineScope? = null
) : ViewModel() {

    private val viewModelScope =
        coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _screenState = MutableStateFlow(LoginScreenState())
    val screenState: StateFlow<LoginScreenState> = _screenState.asStateFlow()

    private val _otpButtonState = MutableStateFlow<ButtonState>(ButtonState.Disabled)
    val otpButtonState: StateFlow<ButtonState> = _otpButtonState.asStateFlow()

    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
    }

    fun updatePhoneNumber(phone: String) {
        _screenState.update { it.copy(phoneNumber = phone) }
        _otpButtonState.value = if (isPhoneNumberValid(phone)) {
            ButtonState.Enabled
        } else {
            ButtonState.Disabled
        }
    }

    fun updateOtp(otp: String) {
        _screenState.update { it.copy(otp = otp) }
    }

    fun sendOtp(activityContext: ActivityContext) {
        val phoneNumber = _screenState.value.phoneNumber
        if (!isPhoneNumberValid(phoneNumber)) return

        viewModelScope.launch {
            _screenState.update { it.copy(uiState = LoginUiState.Loading) }
            sendOtpUseCase(activityContext, phoneNumber).fold(
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
                        it.copy(uiState = LoginUiState.Error(error.message ?: "Failed to send OTP"))
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

            verifyOtpUseCase(state.phoneNumber, state.otp).collect { result ->
                result.onSuccess { _ ->
                    loginUser(state.phoneNumber)
                }
                result.onFailure { error ->
                    _screenState.update {
                        it.copy(uiState = LoginUiState.Error(error.message ?: "Invalid OTP"))
                    }
                }
            }
        }
    }

    private fun loginUser(phoneNumber: String) {
        viewModelScope.launch {
            _screenState.update { it.copy(uiState = LoginUiState.Loading) }
            val user = User(
                createdAt = Clock.System.now().toEpochMilliseconds(),
               phoneNumber = phoneNumber
            )
            loginUserUseCase(user).collect { result ->
                result.onSuccess { userData ->
                    _screenState.update {
                        appDataStoreRepository.setValue(DataStoreKeys.AUTH_TOKEN, userData?.token ?: "")
                        it.copy(uiState = LoginUiState.Success(userData?.token ?: ""))
                    }
                }
                result.onFailure { error ->
                    _screenState.update {
                        it.copy(uiState = LoginUiState.Error(error.message ?: "Login failed"))
                    }
                }
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _screenState.update { it.copy(uiState = LoginUiState.Loading) }

            signInWithGoogleUseCase().fold(
                onSuccess = { userId ->
                    _screenState.update {
                        it.copy(uiState = LoginUiState.Success(userId))
                    }
                },
                onFailure = { error ->
                    _screenState.update {
                        it.copy(
                            uiState = LoginUiState.Error(
                                error.message ?: "Google sign-in failed"
                            )
                        )
                    }
                }
            )
        }
    }

    fun goBack() {
        _screenState.update {
            it.copy(isOtpScreen = false, otp = "", uiState = LoginUiState.Idle)
        }
    }
}