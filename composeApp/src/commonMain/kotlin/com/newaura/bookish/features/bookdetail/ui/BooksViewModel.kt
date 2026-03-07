package com.newaura.bookish.features.bookdetail.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.features.bookdetail.domain.GetBookDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BooksViewModel(private val getBookDetailUseCase: GetBookDetailUseCase) : ViewModel() {

    private val _bookDetailUiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Initial)
    val bookDetailUiState: StateFlow<BookDetailUiState> = _bookDetailUiState

    fun getBookDetail(bookId: String) {
        viewModelScope.launch {
            _bookDetailUiState.value = BookDetailUiState.Loading

            getBookDetailUseCase(bookId)
                .catch { exception ->
                    _bookDetailUiState.value = BookDetailUiState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { bookResult ->
                    bookResult.fold(
                        onFailure = { error ->
                            _bookDetailUiState.value = BookDetailUiState.Error(
                                error.message ?: "Unknown error occurred"
                            )
                        },
                        onSuccess = { bookDetail ->
                            _bookDetailUiState.value = BookDetailUiState.Success(bookDetail)
                        }
                    )
                }
        }
    }
}