package com.newaura.bookish.features.feed

import com.newaura.bookish.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getCurrentUser(): Result<User?>
    suspend fun getUserById(userId: String): Result<User?>
    suspend fun updateProfile(user: User): Result<User>
    suspend fun observeCurrentUser(): Flow<User?>
}