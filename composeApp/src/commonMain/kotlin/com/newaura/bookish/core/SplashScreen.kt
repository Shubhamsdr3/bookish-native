package com.newaura.bookish.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bookish.composeapp.generated.resources.Res
import bookish.composeapp.generated.resources.app_logo
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.core.common.EndSliderGradientBg
import com.newaura.bookish.core.common.StartSliderGradientBg
import com.newaura.bookish.features.auth.LoginScreen
import com.newaura.bookish.features.feed.AuthState
import com.newaura.bookish.features.home.HomeScreen
import com.newaura.bookish.widgets.GradientBox
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val viewModel: SplashViewModel = koinViewModel<SplashViewModel>()
        val authState by viewModel.authState.collectAsState()

        LaunchedEffect(authState) {
            when (authState) {
                is AuthState.Loading -> {
                    // TODO
                }
                is AuthState.Authenticated -> {
                    navigator.push(HomeScreen())
                }

                else -> {
                    navigator.push(LoginScreen())
                }
            }
        }

        GradientBox(
            colors = listOf(StartSliderGradientBg, EndSliderGradientBg),
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(128.dp)
            )
        }
    }
}