package com.newaura.bookish.features.auth.domain

import com.newaura.bookish.core.ActivityContext
import com.newaura.bookish.features.feed.AuthState
import com.newaura.bookish.model.User
import com.newaura.bookish.model.UserResponseDto
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun sendOtp(activityContext: ActivityContext, phoneNumber: String): Result<Boolean>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Flow<Result<Boolean>>
    suspend fun loginUser(user: User): Flow<Result<UserResponseDto?>>
    suspend fun signInWithGoogle(): Result<String>
    suspend fun signOut(): Result<Unit>
    fun observeAuthState(): Flow<AuthState>
    suspend fun isLoggedIn(): Boolean
}