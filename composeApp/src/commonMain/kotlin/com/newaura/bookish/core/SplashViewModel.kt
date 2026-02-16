package com.newaura.bookish.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.core.data.JwtValidationResult
import com.newaura.bookish.core.domain.JwtTokenValidator
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.feed.AuthState
import com.newaura.bookish.features.feed.BookishApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(
    private val userDataStore: UserDataStore,
    private val bookishApiService: BookishApiService
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
                val authToken = userDataStore.getAuthToken()
                val savedUserId = userDataStore.currentUserId
                if (!savedUserId.isNullOrEmpty() && authToken.isNotEmpty()) {

                    val validationResult = withContext(Dispatchers.Default) {
                        JwtTokenValidator.validate(authToken)
                    }
                    when (validationResult) {
                        is JwtValidationResult.Valid -> {
                            bookishApiService.setAuthToken(authToken)
                            _authState.value = AuthState.Authenticated(userId = savedUserId)
                        }
                        is JwtValidationResult.Expired -> {
                            AppLogger.i("JWT Token has expired. User needs to re-login.")
                            userDataStore.clear()
                            _authState.value = AuthState.Unauthenticated
                        }
                        is JwtValidationResult.Invalid -> {
                            AppLogger.i("JWT Token is invalid: ${validationResult.reason}")
                            userDataStore.clear()
                            _authState.value = AuthState.Unauthenticated
                        }
                        is JwtValidationResult.MalformedToken -> {
                            AppLogger.i("JWT Token is malformed: ${validationResult.reason}")
                            userDataStore.clear()
                            _authState.value = AuthState.Unauthenticated
                        }
                    }
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}