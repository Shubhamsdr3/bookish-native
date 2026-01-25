package com.newaura.bookish.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class FeedResponse(
    val isSuccess: Boolean? = null,
    val data: List<FeedData>? = null,
    val currentPage: Int? = null,
    val hasNextPage: Boolean? = null,
    val hasPrevPage: Boolean? = null
)

@Serializable
data class FeedData @OptIn(ExperimentalUuidApi::class) constructor(
    @SerialName("reader")
    val user: Reader? = null,
    val post: FeedPost? = null,
    val isLiked: Boolean? = null,
    val like: PostAction? = null,
    val comment: PostAction? = null,
    val share: PostAction? = null,
    val fallbackId: String = Uuid.random().toString()
)

@Serializable
data class PostAction(
    val icon: String? = null,
    val text: String? = null,
    val count: Int? = null
)

@Serializable
data class FeedPost(
    val id: String? = null,
    val caption: String? = null,
    val images: List<String>? = null
)

@Serializable
data class Reader(
    val userId: String? = null,
    val profileIcon: String? = null,
    val name: String? = null,
    val connections: Int? = null,
    val createdAt: Long? = null,
    val privacy: Int? = null,
    val phoneNumber: String? = null
)