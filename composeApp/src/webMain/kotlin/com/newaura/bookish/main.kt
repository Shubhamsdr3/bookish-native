package com.newaura.bookish

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin {
        modules()
    }
    ComposeViewport {
        App()
    }
}