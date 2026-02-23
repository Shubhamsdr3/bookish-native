package com.newaura.bookish.features.search.data.model

import com.newaura.bookish.model.BookDetail
import kotlinx.serialization.Serializable


@Serializable
data class SearchResultResponse(
    val isSuccess: Boolean,
    val data: SearchResult
)

@Serializable
data class SearchResult(
    val items: List<BookDetail>
)
