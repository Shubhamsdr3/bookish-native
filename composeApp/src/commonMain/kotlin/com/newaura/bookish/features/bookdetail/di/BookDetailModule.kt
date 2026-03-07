package com.newaura.bookish.features.bookdetail.di

import com.newaura.bookish.features.bookdetail.data.BookRepositoryImpl
import com.newaura.bookish.features.bookdetail.domain.BookRepository
import com.newaura.bookish.features.bookdetail.domain.GetBookDetailUseCase
import com.newaura.bookish.features.bookdetail.ui.BooksViewModel
import org.koin.dsl.module

val bookDetailModule = module {
    factory {
        GetBookDetailUseCase(get())
    }

    factory<BookRepository> {
        BookRepositoryImpl(get())
    }

    factory {
        BooksViewModel(get<GetBookDetailUseCase>())
    }
}