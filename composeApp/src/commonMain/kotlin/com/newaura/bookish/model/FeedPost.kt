package com.newaura.bookish.model

import kotlinx.serialization.Serializable

@Serializable
data class FeedData(
    val user: User? = null,
    val post: FeedPost? = null
)

@Serializable
data class FeedResponse(
    val isSuccess: Boolean?  = null,
    val data:  List<FeedData>? = null
)

@Serializable
data class PostAction(
    val icon: String? = null,
    val text: String? = null,
    val count: Int? = null
)

@Serializable
data class FeedPost(
    val caption: String? = null,
    val images: List<String>? = null,
    val like: PostAction? = null,
    val comment: PostAction? = null,
    val share: PostAction? = null
)