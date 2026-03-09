package com.newaura.bookish.features.bookdetail.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReadingProgress(
    val progress: Float? = null,
    val pagesRead: Int? = null,
    val totalPages: Int? = null,
)
