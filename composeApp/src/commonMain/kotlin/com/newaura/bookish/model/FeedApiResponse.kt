package com.newaura.bookish.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedApiResponse(
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("data")
    val data: List<FeedData> = emptyList(),
    @SerialName("currentPage")
    val currentPage: Int = 1,
    @SerialName("totalPages")
    val totalPages: Int = 0,
    @SerialName("totalPosts")
    val totalPosts: Int = 0,
    @SerialName("hasNextPage")
    val hasNextPage: Boolean = false,
    @SerialName("hasPrevPage")
    val hasPrevPage: Boolean = false
) {
    fun getPaginationMetadata(): PaginationMetadata {
        return PaginationMetadata(
            currentPage = currentPage,
            totalPages = totalPages,
            totalPosts = totalPosts,
            hasNextPage = hasNextPage,
            hasPrevPage = hasPrevPage
        )
    }
}

