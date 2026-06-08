package com.newaura.bookish.features.library.data

import kotlinx.serialization.Serializable


@Serializable
data class LibraryBook(
    val bookId: String? = null,
    val title: String? = null,
    val author: String? = null,
    val imageUrl: String? = null,
    val readingStatus: String? = null,
    val readingProgress: Int? = null,
    val totalPages: Int? = null
)