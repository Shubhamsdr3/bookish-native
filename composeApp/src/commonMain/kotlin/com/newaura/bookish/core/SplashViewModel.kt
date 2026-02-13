package com.newaura.bookish.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.feed.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                userDataStore.getAndSetUserId()
                val savedUserId = userDataStore.currentUserId
                if (savedUserId != null) {
                    // Fetch user data
//                    userDataStore.fetchUser(savedUserId)
                    _authState.value = AuthState.Authenticated(userId = savedUserId)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}