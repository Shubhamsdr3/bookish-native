package com.newaura.bookish.features.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.newaura.bookish.features.feed.ui.screens.HomeFeedScreen
import com.newaura.bookish.features.library.LibraryScreen
import com.newaura.bookish.features.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val selectedItem = remember { mutableStateOf(0) }
        
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (selectedItem.value) {
                    0 -> HomeFeedScreen().Content()
                    1 -> LibraryScreen().Content()
                    2 -> ProfileScreen().Content()
                    else -> HomeFeedScreen().Content()
                }
            }
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home") },
                    selected = selectedItem.value == 0,
                    onClick = { selectedItem.value = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LibraryBooks, "My Books") },
                    label = { Text("My Books") },
                    selected = selectedItem.value == 1,
                    onClick = { selectedItem.value = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile") },
                    selected = selectedItem.value == 2,
                    onClick = { selectedItem.value = 2 }
                )
            }
        }
    }
}