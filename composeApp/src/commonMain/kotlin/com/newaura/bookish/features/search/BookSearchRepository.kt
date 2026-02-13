package com.newaura.bookish.features.search

import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.model.BookDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface BookSearchRepository {
    suspend fun searchBooks(query: String, maxResults: Int = 20): Flow<Result<List<BookDetail>>>
}

class BookSearchRepositoryImpl(
    private val apiService: BookishApiService
) : BookSearchRepository {
    override suspend fun searchBooks(query: String, maxResults: Int): Flow<Result<List<BookDetail>>> = flow {
        try {
            val response = apiService.searchBooks(query, maxResults)
            if (response != null && response.items != null) {
                AppLogger.d("Found ${response.items.size} books")
                emit(Result.success(response.items))
            } else {
                AppLogger.e("Empty response from books API")
                emit(Result.success(emptyList()))
            }
        } catch (e: Exception) {
            AppLogger.e("Error searching books", e)
            emit(Result.failure(e))
        }
    }
}

