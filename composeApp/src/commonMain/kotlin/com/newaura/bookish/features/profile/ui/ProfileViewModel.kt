package com.newaura.bookish.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.profile.domain.GetProfileDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userProfileDetailUseCase: GetProfileDetailUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _profileUiState =
        MutableStateFlow<ProfileScreenUiState>(ProfileScreenUiState.Initial)
    val profileUiState: StateFlow<ProfileScreenUiState> = _profileUiState

    fun getUserDetail() {
        viewModelScope.launch {
            _profileUiState.value = ProfileScreenUiState.Loading

            val currentUserId = userDataStore.currentUserId!!

            userProfileDetailUseCase(currentUserId)
                .catch {
                    _profileUiState.value = ProfileScreenUiState.Error(
                        it.message ?: "Unknown error occurred"
                    )
                }
                .collect { userResult ->
                    userResult.fold(
                        onSuccess = { data ->
                            _profileUiState.value = ProfileScreenUiState.Success(data)
                        },
                        onFailure = { error ->
                            _profileUiState.value =
                                ProfileScreenUiState.Error(error.message ?: "Something went wrong")
                        }
                    )
                }

        }
    }

    fun onEditProfileClicked() {

    }
}