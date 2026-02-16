package com.newaura.bookish.core.domain

import com.newaura.bookish.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataStore {

    val currentUserId: String?

    suspend fun getAndSetUserId()

    suspend fun fetchUser()

    fun getUserDetail(): User?

    fun updateUser(user: User)

    suspend fun setUserId(userId: String)

    suspend fun getAuthToken(): String
    suspend fun setAuthToken(authToken: String)

    fun clear()
}