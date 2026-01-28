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
import com.newaura.bookish.core.common.EndSliderGradientBg
import com.newaura.bookish.core.common.StartSliderGradientBg
import com.newaura.bookish.core.domain.AppDataStoreRepository
import com.newaura.bookish.core.domain.DataStoreKeys
import com.newaura.bookish.features.auth.LoginScreen
import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.features.post.ui.CreatePostScreen
import com.newaura.bookish.widgets.GradientBox
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject


class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val appDataStoreRepository = koinInject<AppDataStoreRepository>()
        val apiService = koinInject<BookishApiService>()

        LaunchedEffect(Unit) {
            delay(1000) // 1 sec
            val authToken = appDataStoreRepository.readValue(DataStoreKeys.AUTH_TOKEN)
            if (authToken.isNullOrEmpty()) {
                navigator.push(LoginScreen())
            } else {
                apiService.setAuthToken(authToken)
                navigator.push(CreatePostScreen())
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