package com.newaura.bookish.core

import android.app.Activity
import android.app.Application
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable

actual typealias ApplicationContext = Application

actual typealias ActivityContext = Activity

@Composable
actual fun getActivityContext(): ActivityContext {
    return LocalActivity.current as ActivityContext
}