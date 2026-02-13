package com.newaura.bookish.features.search.ui

import androidx.lifecycle.ViewModel
import com.newaura.bookish.features.search.domain.SearchBooksUseCase
import com.newaura.bookish.model.BookDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface BookSearchUiState {
    data object Idle : BookSearchUiState
    data object Loading : BookSearchUiState
    data class Success(val books: List<BookDetail>) : BookSearchUiState
    data class Error(val message: String) : BookSearchUiState
}

data class BookSearchScreenState(
    val uiState: BookSearchUiState = BookSearchUiState.Idle,
    val searchQuery: String = "",
    val selectedBook: BookDetail? = null
)

class SearchBooksViewModel(
    private val searchBooksUseCase: SearchBooksUseCase,
    coroutineScope: CoroutineScope? = null
) : ViewModel() {
    private val viewModelScope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _screenState = MutableStateFlow(BookSearchScreenState())
    val screenState: StateFlow<BookSearchScreenState> = _screenState.asStateFlow()

    fun searchBooks(query: String) {
        if (query.isEmpty()) {
            _screenState.update { it.copy(uiState = BookSearchUiState.Idle) }
            return
        }

        _screenState.update { it.copy(searchQuery = query, uiState = BookSearchUiState.Loading) }

        viewModelScope.launch {
            searchBooksUseCase(query).collect { result ->
                result.fold(
                    onSuccess = { books ->
                        _screenState.update {
                            it.copy(
                                uiState = BookSearchUiState.Success(books),
                                searchQuery = query
                            )
                        }
                    },
                    onFailure = { error ->
                        _screenState.update {
                            it.copy(
                                uiState = BookSearchUiState.Error(
                                    error.message ?: "Failed to search books"
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    fun selectBook(book: BookDetail?) {
        _screenState.update { it.copy(selectedBook = book) }
    }

    fun clearSearch() {
        _screenState.update {
            BookSearchScreenState(
                uiState = BookSearchUiState.Idle,
                searchQuery = ""
            )
        }
    }
}

