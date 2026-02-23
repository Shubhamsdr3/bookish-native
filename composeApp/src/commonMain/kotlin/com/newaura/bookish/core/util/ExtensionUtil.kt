package com.newaura.bookish.core.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun String.toCamelCase(): String {
    return this.lowercase()
        .replaceFirstChar { it.uppercase() }
}

fun Modifier.customImePadding(extra: Dp = 0.dp): Modifier = composed {
    val imeBottom = with(LocalDensity.current) {
        WindowInsets.ime.getBottom(this).toDp()
    }
    this.then(Modifier.padding(bottom = imeBottom + extra))
}