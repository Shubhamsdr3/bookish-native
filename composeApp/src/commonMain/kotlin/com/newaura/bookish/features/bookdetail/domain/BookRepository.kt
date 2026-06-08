package com.newaura.bookish.features.bookdetail.domain

import com.newaura.bookish.features.library.data.LibraryBook
import com.newaura.bookish.model.BookDetail
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    suspend fun fetchBookDetail(bookId: String): Flow<Result<BookDetail>>

    suspend fun fetchLibraryBooks(userId: String): Flow<Result<List<LibraryBook>>>
}
