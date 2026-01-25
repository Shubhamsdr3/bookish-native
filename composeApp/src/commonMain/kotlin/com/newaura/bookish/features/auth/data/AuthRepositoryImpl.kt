package com.newaura.bookish.features.auth.data

import com.newaura.bookish.core.ActivityContext
import com.newaura.bookish.features.auth.domain.AuthRepository
import com.newaura.bookish.features.auth.domain.PhoneAuthService
import com.newaura.bookish.features.feed.AuthState
import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.model.User
import com.newaura.bookish.model.UserResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepositoryImpl(
    private val authService: PhoneAuthService,
    private val apiService: BookishApiService,
) : AuthRepository {

    override suspend fun sendOtp(
        activityContext: ActivityContext,
        phoneNumber: String
    ): Result<Boolean> {
        authService.sendVerificationCode(activityContext, phoneNumber) { result ->
            result.onSuccess { id ->
                println("Shubham ==> SMS Sent successfully. ID: $id")
            }
            result.onFailure { error ->
                println("Shubham ==> Failed to send SMS: ${error.message}")
            }
        }
        return Result.success(true)
    }

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String
    ): Flow<Result<Boolean>> = flow {
        val result = suspendCancellableCoroutine { continuation ->
            authService.verifyCode(otp, callback = { result ->
                result.onSuccess { isSuccess ->
                    continuation.resume(Result.success(isSuccess))
                }
                result.onFailure { error ->
                    continuation.resume(Result.failure(error))
                }
            })
        }
        emit(result)
    }

    override suspend fun loginUser(user: User): Flow<Result<UserResponseDto?>> = flow {
        try {
            val response = apiService.loginUser(user)
            response.fold(
                onSuccess = { userDataResponse ->
                    val userData = userDataResponse.data
                    apiService.setAuthToken(userData?.token ?: "")
                    emit(Result.success(userData))
                },
                onFailure = { error ->
                    emit(Result.failure(error))
                }
            )
        } catch (ex: Exception) {
            emit(Result.failure(ex))
        }
    }

    override suspend fun signInWithGoogle(): Result<String> {
        return Result.success("true")
    }

    override suspend fun signOut(): Result<Unit> {
        return Result.success(Unit)
    }

    override fun observeAuthState(): Flow<AuthState> {
        return flowOf(AuthState.Authenticated("userId"))
    }

    override suspend fun isLoggedIn(): Boolean {
        return true
    }
}