package com.newaura.bookish.features.search.ui

import androidx.lifecycle.ViewModel
import com.newaura.bookish.features.search.domain.SearchBooksUseCase
import com.newaura.bookish.features.search.domain.model.BookSearchScreenState
import com.newaura.bookish.features.search.domain.model.BookSearchUiState
import com.newaura.bookish.model.BookDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class SearchBooksViewModel(
    private val searchBooksUseCase: SearchBooksUseCase,
    coroutineScope: CoroutineScope? = null
) : ViewModel() {
    private val viewModelScope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _screenState = MutableStateFlow(BookSearchScreenState())
    val screenState: StateFlow<BookSearchScreenState> = _screenState.asStateFlow()

    private var searchJob: Job? = null
    private val debounceDelayMs = 200L

    fun onSearchQueryChanged(query: String) {
        _screenState.update { it.copy(searchQuery = query) }

        // Cancel previous search job
        searchJob?.cancel()

        if (query.isEmpty()) {
            _screenState.update { it.copy(uiState = BookSearchUiState.Idle) }
            return
        }

        // Launch new search job with debounce
        searchJob = viewModelScope.launch {
            delay(debounceDelayMs)
            searchBooks(query)
        }
    }

    private suspend fun searchBooks(query: String) {
        _screenState.update { it.copy(uiState = BookSearchUiState.Loading) }

        searchBooksUseCase(query).collect { result ->
            result.fold(
                onSuccess = { bookResponse ->
                    val books = bookResponse.data.items
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

