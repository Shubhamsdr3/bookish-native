package com.newaura.bookish

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.newaura.bookish.core.Context
import com.newaura.bookish.core.common.AppTheme
import com.newaura.bookish.core.data.AppDataStoreRepositoryManager
import com.newaura.bookish.core.domain.AppDataStoreRepository
import com.newaura.bookish.features.auth.di.authDataModule
import com.newaura.bookish.features.auth.di.authDomainModule
import com.newaura.bookish.features.auth.di.createAuthService
import com.newaura.bookish.features.auth.domain.PhoneAuthService
import com.newaura.bookish.features.feed.di.commonModule
import com.newaura.bookish.features.feed.di.feedDataModule
import com.newaura.bookish.features.home.di.homePresentationModule
import com.newaura.bookish.features.post.di.createPostModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

@Composable
@Preview
fun App() {
    AppTheme {
        Navigator(
            screen = SplashScreen()
        )
    }
}

fun initKoin(context: Context, config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        val authServiceModule = module {
            single<PhoneAuthService> { createAuthService(context) }
        }
        val dataStoreModule = module {
            single<AppDataStoreRepository> { AppDataStoreRepositoryManager(context) }
        }

        modules(
            commonModule,
            dataStoreModule,
            homePresentationModule,
            feedDataModule,
            authDataModule,
            authDomainModule,
            authServiceModule,
            createPostModule
        )
    }
}