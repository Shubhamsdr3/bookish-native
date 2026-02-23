package com.newaura.bookish.features.search.di

import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.features.search.data.SearchRepositoryImpl
import com.newaura.bookish.features.search.domain.ISearchRepository
import com.newaura.bookish.features.search.domain.SearchBooksUseCase
import com.newaura.bookish.features.search.ui.SearchBooksViewModel
import org.koin.dsl.module

val searchModule = module {
    single<ISearchRepository> {
        SearchRepositoryImpl(
            bookApiService = get<BookishApiService>()
        )
    }

    factory { SearchBooksUseCase(get()) }

    factory { SearchBooksViewModel(get()) }
}

