package com.newaura.bookish.features.feed.domain.ui.screens

import androidx.compose.runtime.*
import com.newaura.bookish.features.feed.domain.ui.HomeFeedViewModel
import com.newaura.bookish.features.feed.domain.ui.LoginViewModel

sealed class Screen {
    data object Splash : Screen()
    data object Login : Screen()
    data object Home : Screen()
    data class FeedDetail(val feedId: String) : Screen()
    data object Profile : Screen()
    data object CreatePost : Screen()
}

@Composable
fun BookishNavHost(
    loginViewModel: LoginViewModel,
    homeFeedViewModel: HomeFeedViewModel,
    isLoggedIn: Boolean
) {
    var currentScreen by remember { mutableStateOf<Screen>(if (isLoggedIn) Screen.Home else Screen.Login) }

    when (currentScreen) {
        is Screen.Login -> {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { currentScreen = Screen.Home }
            )
        }

        is Screen.Home -> {
            HomeFeedScreen(
                viewModel = homeFeedViewModel,
                onFeedClick = { feed -> 
                    // Navigate to feed detail
                },
                onProfileClick = { currentScreen = Screen.Profile },
                onCreatePostClick = { currentScreen = Screen.CreatePost }
            )
        }

        else -> {
            // Handle other screens
        }
    }
}