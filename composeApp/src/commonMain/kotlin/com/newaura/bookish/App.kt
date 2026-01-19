package com.newaura.bookish

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.newaura.bookish.features.feed.domain.di.commonModule
import com.newaura.bookish.features.feed.domain.di.feedDataModule
import com.newaura.bookish.features.home.di.homePresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigator(
            screen = SplashScreen()
        )
    }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            commonModule,
            homePresentationModule,
            feedDataModule
        )
    }
}