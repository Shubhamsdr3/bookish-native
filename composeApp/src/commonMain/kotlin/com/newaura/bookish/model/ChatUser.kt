package com.newaura.bookish.model

import kotlinx.serialization. Serializable

@Serializable
data class ChatUser(
    val id: String,
    val name: String?  = null,
    val profileIcon: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: Long?  = null,
    val isOnline: Boolean = false
)

@Serializable
data class ChatMessage(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp:  Long,
    val isRead:  Boolean = false
)