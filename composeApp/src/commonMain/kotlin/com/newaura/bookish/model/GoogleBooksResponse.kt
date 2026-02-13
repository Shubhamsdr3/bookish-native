package com.newaura.bookish.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleBooksResponse(
    @SerialName("items")
    val items: List<BookDetail>? = null,
    @SerialName("totalItems")
    val totalItems: Int = 0
)

