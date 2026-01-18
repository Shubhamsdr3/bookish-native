package com.newaura.bookish.features.feed.domain

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun sendOtp(phoneNumber: String): Result<Boolean>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<String> // Returns userId
    suspend fun signInWithGoogle(): Result<String>
    suspend fun signOut(): Result<Unit>
    fun observeAuthState(): Flow<AuthState>
    suspend fun isLoggedIn(): Boolean
}