package com.newaura.bookish.features.library.domain

import com.newaura.bookish.features.bookdetail.domain.BookRepository
import com.newaura.bookish.features.library.data.LibraryBook
import kotlinx.coroutines.flow.Flow

class GetLibraryBookUseCase(private val bookRepository: BookRepository) {

    suspend operator fun invoke(userId: String): Flow<Result<List<LibraryBook>>> {
        return bookRepository.fetchLibraryBooks(userId)
    }
}