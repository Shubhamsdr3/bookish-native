package com.newaura.bookish

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.newaura.bookish.core.Context
import com.newaura.bookish.core.SplashScreen
import com.newaura.bookish.core.common.AppTheme
import com.newaura.bookish.core.config.JwtConfig
import com.newaura.bookish.core.data.AppDataStoreRepositoryManager
import com.newaura.bookish.core.data.UserDataStoreImpl
import com.newaura.bookish.core.domain.AppDataStoreRepository
import com.newaura.bookish.core.domain.JwtTokenValidator
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.auth.di.authDataModule
import com.newaura.bookish.features.auth.di.authDomainModule
import com.newaura.bookish.features.auth.di.createAuthService
import com.newaura.bookish.features.auth.domain.PhoneAuthService
import com.newaura.bookish.features.feed.UserRepository
import com.newaura.bookish.features.feed.di.commonModule
import com.newaura.bookish.features.feed.di.feedDataModule
import com.newaura.bookish.features.home.di.homePresentationModule
import com.newaura.bookish.features.post.di.createPostModule
import com.newaura.bookish.features.post.di.imageUploadModule
import com.newaura.bookish.features.post.domain.FilePicker
import com.newaura.bookish.features.post.ui.FilePickerImpl
import com.newaura.bookish.features.search.di.searchModule
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

        val userDataStore = module {
            single<UserDataStore> { UserDataStoreImpl(
                get<UserRepository>(),
                get<AppDataStoreRepository>())
            }
        }

        val filePicker = module {
            single<FilePicker> { FilePickerImpl() }
        }

        modules(
            commonModule,
            dataStoreModule,
            userDataStore,
            homePresentationModule,
            feedDataModule,
            authDataModule,
            authDomainModule,
            authServiceModule,
            imageUploadModule,
            createPostModule,
            searchModule,
            filePicker
        )
    }

    // Initialize JWT validator with backend public key
    initializeJwtValidator()
}

private fun initializeJwtValidator() {
    JwtTokenValidator.setPublicKey(JwtConfig.BACKEND_PUBLIC_KEY)
}