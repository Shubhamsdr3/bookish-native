package com.newaura.bookish.features.profile.ui

import com.newaura.bookish.features.profile.data.ProfileResponse

sealed class ProfileScreenUiState {
    object Loading : ProfileScreenUiState()
    data class Success(val profileData: ProfileResponse?) : ProfileScreenUiState()
    data class Error(val message: String) : ProfileScreenUiState()
    object Initial : ProfileScreenUiState()
}