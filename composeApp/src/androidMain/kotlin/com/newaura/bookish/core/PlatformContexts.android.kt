package com.newaura.bookish.core

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable

@Composable
actual fun getActivityContext(): ActivityContext {
    val context = LocalActivity.current as ActivityContext
    return context
}