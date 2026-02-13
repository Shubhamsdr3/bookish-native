package com.newaura.bookish.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginationMetadata(
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val totalPosts: Int = 0,
    val hasNextPage: Boolean = false,
    val hasPrevPage: Boolean = false
)

