package com.newaura.bookish.features.bookdetail.ui

import com.newaura.bookish.model.BookDetail

sealed class BookDetailUiState {
    object Loading : BookDetailUiState()
    data class Success(val bookDetail: BookDetail) : BookDetailUiState()
    data class Error(val message: String) : BookDetailUiState()
    object Initial : BookDetailUiState()
}