package com.newaura.bookish.core.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class WebViewScreen(private val webUrl: String) : Screen {

    @Composable
    override fun Content() {
        WebViewWidget(webUrl)
    }
}