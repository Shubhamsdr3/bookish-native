package com.newaura.bookish.features.bookdetail.domain

import com.newaura.bookish.model.BookDetail
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    suspend fun fetchBookDetail(bookId: String): Flow<Result<BookDetail>>
}
