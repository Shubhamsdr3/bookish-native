package com.newaura.bookish.features.feed.domain

import kotlinx.coroutines.flow. Flow

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}