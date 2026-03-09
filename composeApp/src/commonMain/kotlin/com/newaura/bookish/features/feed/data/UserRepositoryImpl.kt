package com.newaura.bookish.features.feed.data

import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.features.feed.UserRepository
import com.newaura.bookish.features.profile.data.ProfileResponse
import com.newaura.bookish.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val apiService: BookishApiService,
): UserRepository {

    override suspend fun getUserProfile(userId: String): Flow<Result<ProfileResponse?>> = flow {
         try {
            val response = apiService.getUserProfile(userId)
            if (response?.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception("Failed to fetch current user")))
            }
        } catch (ex: Exception) {
             emit(Result.failure(ex))
        }
    }

    override suspend fun getUserById(userId: String): Flow<Result<User?>>  {
        TODO("Not implemented")
    }

    override suspend fun updateProfile(user: User): Flow<Result<User>>  {
        TODO("Not implemented")
    }

    override suspend fun observeCurrentUser(): Flow<User?> {
        TODO("Not implemented")
    }
}