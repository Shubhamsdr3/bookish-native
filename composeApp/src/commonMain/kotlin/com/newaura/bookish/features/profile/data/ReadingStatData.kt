package com.newaura.bookish.features.profile.data

import com.newaura.bookish.model.User
import kotlinx.serialization.Serializable

enum class StatsType(val value: String) {
    READING("Reading"),
    COMPLETED("Completed"),
    LIKES("Likes"),
    POSTS("Posts")
}

@Serializable
data class ProfileResponse(
    val user: User? = null,
    val stats: List<ReadingStatData>? = null,
    val readingGoals: List<ReadingGoal>? = null
)

@Serializable
data class ReadingStatData(
    val label: String? = null,
    val imageUrl: String? = null,
    val count: Int = 0,
    val iconColor: String? = null,
    val type: String? = null
) {
    val statsType: StatsType?
        get() = StatsType.entries.find { it.value == type }
}

@Serializable
data class ReadingGoal(
    val year: Int? = null,
    val title: String? = null,
    val booksRead: Int = 0,
    val totalBooks: Int = 0
)