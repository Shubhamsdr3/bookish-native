package com.newaura.bookish.core

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable

@Composable
actual fun getActivityContext(): ActivityContext {
    return LocalActivity.current as ActivityContext
}