package com.newaura.bookish.features.feed.domain

import kotlinx.coroutines.flow.Flow

class SendOtpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String): Result<Boolean> {
        return authRepository.sendOtp(phoneNumber)
    }
}

class VerifyOtpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String, otp: String): Result<String> {
        return authRepository.verifyOtp(phoneNumber, otp)
    }
}

class SignInWithGoogleUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<String> {
        return authRepository.signInWithGoogle()
    }
}

class ObserveAuthStateUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<AuthState> {
        return authRepository.observeAuthState()
    }
}