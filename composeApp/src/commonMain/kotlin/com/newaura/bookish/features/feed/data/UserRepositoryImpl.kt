package com.newaura.bookish.features.feed.data

import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.features.feed.UserRepository
import com.newaura.bookish.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val apiService: BookishApiService,
): UserRepository {

    override suspend fun getCurrentUser(): Flow<Result<User?>> = flow {
        TODO("Not implemented")
    }

    override suspend fun getUserById(userId: String): Result<User?> {
        TODO("Not implemented")
    }

    override suspend fun updateProfile(user: User): Result<User> {
        TODO("Not implemented")
    }

    override suspend fun observeCurrentUser(): Flow<User?> {
        TODO("Not implemented")
    }
}