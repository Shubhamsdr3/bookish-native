package com.newaura.bookish.features.feed

import com.newaura.bookish.features.profile.data.ProfileResponse
import com.newaura.bookish.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(userId: String): Flow<Result<ProfileResponse?>>
    suspend fun getUserById(userId: String): Flow<Result<User?>>
    suspend fun updateProfile(user: User): Flow<Result<User>>
    suspend fun observeCurrentUser(): Flow<User?>
}