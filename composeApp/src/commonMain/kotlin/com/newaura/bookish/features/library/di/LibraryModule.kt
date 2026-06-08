package com.newaura.bookish.features.library.di

import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.bookdetail.domain.BookRepository
import com.newaura.bookish.features.library.domain.GetLibraryBookUseCase
import com.newaura.bookish.features.library.ui.LibraryViewModel
import org.koin.dsl.module

val libraryModule = module {

    factory { GetLibraryBookUseCase(get<BookRepository>()) }

    factory { LibraryViewModel(get<UserDataStore>(), get<GetLibraryBookUseCase>()) }

}