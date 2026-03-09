package com.newaura.bookish.core.data

import com.newaura.bookish.core.domain.AppDataStoreRepository
import com.newaura.bookish.core.domain.DataStoreKeys
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.feed.UserRepository
import com.newaura.bookish.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserDataStoreImpl(
    private val userRepository: UserRepository,
    private val appDataStoreRepository: AppDataStoreRepository
) : UserDataStore {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    private val _currentUserId = MutableStateFlow<String?>(null)

    override val currentUserId: String? get() = _currentUserId.value

    override suspend fun getAndSetUserId() {
        val userId = appDataStoreRepository.readValue(DataStoreKeys.USER_ID)
        _currentUserId.value = userId
    }

    override suspend fun fetchUser() {
        try {
            val userData = userRepository.getUserById("")
//            _user.value = userData
        } catch (e: Exception) {
            // Handle error as needed
            throw e
        }
    }

    override fun getUserDetail(): User? = _user.value

    override fun updateUser(user: User) {
        _user.value = user
    }

    override suspend fun setUserId(userId: String) {
        _currentUserId.value = userId
        appDataStoreRepository.setValue(DataStoreKeys.USER_ID, userId)
    }

    override suspend fun getAuthToken(): String {
        return appDataStoreRepository.readValue(DataStoreKeys.AUTH_TOKEN) ?: ""
    }

    override suspend fun setAuthToken(authToken: String) {
        appDataStoreRepository.setValue(DataStoreKeys.AUTH_TOKEN, authToken)
    }

    override fun clear() {
        _user.value = null
    }
}