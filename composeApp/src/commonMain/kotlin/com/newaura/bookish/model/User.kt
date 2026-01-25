package com.newaura.bookish.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(
    val token: String? = null,
    val user: User? = null
)


@Serializable
data class User(
    val userId: String? = null,
    val profileIcon: String?  = null,
    val name: String?  = null,
    val connections: Int? = null,
    val createdAt: Long? = null,
    val privacy: Int? = null,
    val phoneNumber: String? = null
)