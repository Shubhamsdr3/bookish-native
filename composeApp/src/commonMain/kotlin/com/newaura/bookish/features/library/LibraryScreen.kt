package com.newaura.bookish.features.library

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class LibraryScreen: Screen {


    @Composable
    override fun Content() {
        Text("My Library")
    }
}