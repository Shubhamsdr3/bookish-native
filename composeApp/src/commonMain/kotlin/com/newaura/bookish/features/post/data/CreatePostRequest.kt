package com.newaura.bookish.features.post.data

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
    val userId: String,
    val isLiked: Boolean,
    val post: PostData
)

@Serializable
data class PostData(
    val caption: String,
    val images: List<String>,
    val bookName: String,
    val bookLink: String,
    val bookId: String,
    val bookCategories: List<String>,
    val postType: String
)
