package com.newaura.bookish.features.bookdetail.data.dto

import com.newaura.bookish.model.User
import kotlinx.serialization.Serializable


@Serializable
data class BookReviews(
    val user: User? = null,
    val rating: Float? = null,
    val reviewContent: String? = null,
    val reactions: Int? = null
)