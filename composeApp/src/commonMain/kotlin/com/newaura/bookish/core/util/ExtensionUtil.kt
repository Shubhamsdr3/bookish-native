package com.newaura.bookish.core.util

fun String.toCamelCase(): String {
    return this.lowercase()
        .replaceFirstChar { it.uppercase() }
}