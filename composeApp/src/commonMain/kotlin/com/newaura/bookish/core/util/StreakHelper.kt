package com.newaura.bookish.core.util

import com.newaura.bookish.core.data.ActivityLevel
import com.newaura.bookish.core.data.toActivityLevel
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * Builds a list of week columns, each containing 7 ActivityLevels (Sun→Sat).
 * Days outside the year are filled with NONE.
 */
fun buildWeekColumns(
    year: Int,
    activityData: Map<LocalDate, Int>
): List<List<ActivityLevel>> {
    val weeks = mutableListOf<List<ActivityLevel>>()
    val jan1 = LocalDate(year, 1, 1)
    val dec31 = LocalDate(year, 12, 31)

    // Start from the Sunday on or before Jan 1
    val dayOfWeekValue = jan1.dayOfWeek.isoDayNumber % 7 // Sun=0
    var current = jan1.minus(DatePeriod(days = dayOfWeekValue))

    while (current <= dec31) {
        val week = (0..6).map { offset ->
            val day = current.plus(DatePeriod(days = offset))
            if (day.year == year) {
                (activityData[day] ?: 0).toActivityLevel()
            } else {
                ActivityLevel.NONE
            }
        }
        weeks.add(week)
        current = current.plus(DatePeriod(days = 7))
    }
    return weeks
}

/**
 * Returns a map of weekIndex → month abbreviation
 * for the first week a new month appears.
 */
fun buildMonthLabels(weeks: List<List<ActivityLevel>>): Map<Int, String> {
    val monthAbbreviations = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    val result = mutableMapOf<Int, String>()
    val totalWeeks = weeks.size
    for (month in 0..11) {
        val approxIndex = (month * totalWeeks) / 12
        result[approxIndex] = monthAbbreviations[month]
    }
    return result
}

/**
 * Calculates the longest streak of consecutive active days.
 */
fun calculateMaxStreak(activityData: Map<LocalDate, Int>): Int {
    if (activityData.isEmpty()) return 0
    val sortedDates = activityData
        .filter { it.value > 0 }
        .keys
        .sorted()

    var maxStreak = 0
    var currentStreak = 0
    var prevDate: LocalDate? = null

    sortedDates.forEach { date ->
        currentStreak = if (prevDate != null &&
            date == prevDate.plus(DatePeriod(days = 1))
        ) {
            currentStreak + 1
        } else {
            1
        }
        if (currentStreak > maxStreak) maxStreak = currentStreak
        prevDate = date
    }
    return maxStreak
}