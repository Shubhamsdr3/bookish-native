package com.newaura.bookish.features.search.domain.model

import com.newaura.bookish.model.BookDetail

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