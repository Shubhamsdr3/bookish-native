package com.newaura.bookish.features.mybooks

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class MyBookScreen : Screen {

    @Composable
    override fun Content() {
        Text("My Books")
    }
}