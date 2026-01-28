package com.newaura.bookish.core.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

object DateTimeUtility {

    fun getFormattedDateTime(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val istTimeZone = TimeZone.of("Asia/Kolkata")
        val dateTime = instant.toLocalDateTime(istTimeZone)
        return "${dateTime.month.number.toString().padStart(2, '0')}/${
            dateTime.day.toString().padStart(2, '0')
        }/${dateTime.year}"
    }
}