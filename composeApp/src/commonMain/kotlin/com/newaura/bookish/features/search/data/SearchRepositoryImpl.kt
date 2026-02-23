package com.newaura.bookish.features.search.data

import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.features.search.data.model.SearchResultResponse
import com.newaura.bookish.features.search.domain.ISearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(private val bookApiService: BookishApiService): ISearchRepository {

    override suspend fun searchBooks(query: String): Flow<Result<SearchResultResponse>> = flow {
        try {
            if (query.isBlank()) {
                emit(Result.failure(Exception("Search query cannot be empty")))
                return@flow
            }
            val response = bookApiService.searchBook(query)
            if (response != null && response.data != null) {
                emit(Result.success(response.data!!))
            } else {
                val errorMessage = response?.message ?: "Something went wrong"
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

}