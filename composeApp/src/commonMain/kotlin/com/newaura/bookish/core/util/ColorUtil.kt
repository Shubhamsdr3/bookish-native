package com.newaura.bookish.core.util

import androidx.compose.ui.graphics.Color

fun String.toColor(): Color {
    val hex = removePrefix("#")
    val colorLong = when (hex.length) {
        6 -> "FF$hex".toLong(16)  // Add full opacity
        8 -> hex.toLong(16)
        else -> throw IllegalArgumentException("Invalid color: $this")
    }
    return Color(colorLong.toInt())
}