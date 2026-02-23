package com.newaura.bookish.features.search.domain

import com.newaura.bookish.features.search.data.model.SearchResultResponse
import kotlinx.coroutines.flow.Flow

class SearchBooksUseCase(
    private val bookSearchRepository: ISearchRepository
) {
    suspend operator fun invoke(query: String): Flow<Result<SearchResultResponse>> {
        return bookSearchRepository.searchBooks(query)
    }
}

