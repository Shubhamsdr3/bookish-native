package com.newaura.bookish.features.bookdetail.domain

import com.newaura.bookish.model.BookDetail
import kotlinx.coroutines.flow.Flow

class GetBookDetailUseCase(private val bookRepository: BookRepository) {

    suspend operator fun invoke(bookId: String): Flow<Result<BookDetail>> {
        return bookRepository.fetchBookDetail(bookId)
    }
}