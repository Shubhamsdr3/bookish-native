package com.newaura.bookish.features.search.domain

import com.newaura.bookish.features.search.BookSearchRepository
import com.newaura.bookish.model.BookDetail
import kotlinx.coroutines.flow.Flow

class SearchBooksUseCase(
    private val bookSearchRepository: BookSearchRepository
) {
    suspend operator fun invoke(query: String, maxResults: Int = 20): Flow<Result<List<BookDetail>>> {
        return bookSearchRepository.searchBooks(query, maxResults)
    }
}

