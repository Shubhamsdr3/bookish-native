package com.newaura.bookish.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePostResponse(
    @SerialName("isSuccess")
    val isSuccess: Boolean? = null,
    @SerialName("data")
    val data: FeedData? = null,
    @SerialName("message")
    val message: String? = null
)

