package com.newaura.bookish.features.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.library.domain.GetLibraryBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val userDataStore: UserDataStore,
    private val getLibraryBookUseCase: GetLibraryBookUseCase
) : ViewModel() {

    private val _libraryUiScreenState =
        MutableStateFlow<LibraryScreenState>(LibraryScreenState.Idle)

    val libraryUiScreenState: StateFlow<LibraryScreenState> = _libraryUiScreenState

    fun fetchBooks() {
        viewModelScope.launch {
            _libraryUiScreenState.value = LibraryScreenState.Loading
            val userId = userDataStore.currentUserId!!
            getLibraryBookUseCase(userId)
                .catch {
                    _libraryUiScreenState.value =
                        LibraryScreenState.OnError(it.message ?: "Unknown error")

                }.collect { result ->
                    result.fold(
                        onSuccess = {
                            _libraryUiScreenState.value = LibraryScreenState.OnSuccess(it)
                        },
                        onFailure = {
                            _libraryUiScreenState.value =
                                LibraryScreenState.OnError(it.message ?: "Unknown error")
                        }
                    )

                }
        }
    }

}