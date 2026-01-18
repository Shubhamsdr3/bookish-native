package com.newaura.bookish

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bookish.composeapp.generated.resources.Res
import bookish.composeapp.generated.resources.app_logo
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.features.home.HomeScreen
import com.newaura.bookish.widgets.GradientBox
import com.newaura.bookish.common.StartSliderGradientBg
import com.newaura.bookish.common.EndSliderGradientBg
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource


class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            delay(1000) // 1 sec
            navigator.push(HomeScreen())
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