package com.newaura.bookish.features.bookdetail.data

import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.features.bookdetail.domain.BookRepository
import com.newaura.bookish.model.BookDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BookRepositoryImpl(private val apiService: BookishApiService) : BookRepository {

    override suspend fun fetchBookDetail(bookId: String): Flow<Result<BookDetail>> = flow {
        try {
            val response = apiService.fetchBookDetail(bookId)
            AppLogger.d("Fetched book detail response: $response")

            if (response != null && response.isSuccess) {
                val bookDetail = response.data?.data
                emit(Result.success(bookDetail!!))
            } else {
                emit(Result.failure(Exception(response?.errorMessage ?: "Unknown error occurred")))
            }
        } catch (e: Exception) {
            AppLogger.e("Error fetching book detail: ${e.message}", e)
            emit(Result.failure(e))
        }
    }
}