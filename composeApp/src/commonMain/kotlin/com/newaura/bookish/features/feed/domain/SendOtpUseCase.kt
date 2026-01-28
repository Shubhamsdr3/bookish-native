package com.newaura.bookish.features.feed.domain

import com.newaura.bookish.core.ActivityContext
import com.newaura.bookish.features.auth.domain.AuthRepository
import com.newaura.bookish.features.feed.AuthState
import com.newaura.bookish.model.User
import com.newaura.bookish.model.UserResponseDto
import kotlinx.coroutines.flow.Flow

class SendOtpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(activityContext: ActivityContext, phoneNumber: String): Result<Boolean> {
        return authRepository.sendOtp(activityContext, phoneNumber)
    }
}

class VerifyOtpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String, otp: String): Flow<Result<Boolean>> {
        return authRepository.verifyOtp(phoneNumber, otp)
    }
}

class SignInWithGoogleUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<String> {
        return authRepository.signInWithGoogle()
    }
}

class LoginUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(user: User): Flow<Result<UserResponseDto?>> {
        return authRepository.loginUser(user)
    }
}

class ObserveAuthStateUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<AuthState> {
        return authRepository.observeAuthState()
    }
}