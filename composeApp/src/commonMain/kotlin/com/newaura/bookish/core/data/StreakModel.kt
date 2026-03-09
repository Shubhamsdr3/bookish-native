package com.newaura.bookish.core.data
import kotlinx.datetime.LocalDate

data class DayActivity(
    val date: LocalDate,
    val pagesRead: Int
)

enum class ActivityLevel {
    NONE, LOW, MEDIUM, HIGH, VERY_HIGH
}

fun Int.toActivityLevel(): ActivityLevel = when {
    this == 0    -> ActivityLevel.NONE
    this <= 10   -> ActivityLevel.LOW
    this <= 30   -> ActivityLevel.MEDIUM
    this <= 60   -> ActivityLevel.HIGH
    else         -> ActivityLevel.VERY_HIGH
}