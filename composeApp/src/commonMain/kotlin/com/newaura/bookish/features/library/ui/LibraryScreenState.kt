package com.newaura.bookish.features.library.ui

import com.newaura.bookish.features.library.data.LibraryBook

sealed class LibraryScreenState {

    object Loading: LibraryScreenState()
    data class OnError(val errorMessage: String): LibraryScreenState()
    data class OnSuccess(val books: List<LibraryBook>): LibraryScreenState()
    object Idle: LibraryScreenState()
}