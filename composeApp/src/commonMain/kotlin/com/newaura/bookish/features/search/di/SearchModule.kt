package com.newaura.bookish.features.search.di

import com.newaura.bookish.features.search.BookSearchRepository
import com.newaura.bookish.features.search.BookSearchRepositoryImpl
import com.newaura.bookish.features.search.domain.SearchBooksUseCase
import com.newaura.bookish.features.search.ui.SearchBooksViewModel
import org.koin.dsl.module

val searchModule = module {
    single<BookSearchRepository> {
        BookSearchRepositoryImpl(apiService = get())
    }

    factory { SearchBooksUseCase(get()) }

    factory { SearchBooksViewModel(get()) }
}

