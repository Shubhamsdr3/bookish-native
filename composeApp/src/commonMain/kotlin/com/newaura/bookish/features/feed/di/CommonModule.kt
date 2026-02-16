package com.newaura.bookish.features.feed.di

import com.newaura.bookish.core.SplashViewModel
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.FeedRepository
import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.features.feed.FeedRepositoryImpl
import com.newaura.bookish.features.feed.UserRepository
import com.newaura.bookish.features.feed.data.UserRepositoryImpl
import com.newaura.bookish.features.feed.domain.GetHomeFeedUseCase
import com.newaura.bookish.features.feed.KtorBookishApiService
import com.newaura.bookish.features.feed.domain.SendOtpUseCase
import com.newaura.bookish.features.feed.domain.SignInWithGoogleUseCase
import com.newaura.bookish.features.feed.domain.VerifyOtpUseCase
import com.newaura.bookish.features.feed.ui.HomeFeedViewModel
import com.newaura.bookish.features.feed.ui.LoginViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module


val commonModule = module {

    // Network
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }

    single<BookishApiService> {
        KtorBookishApiService()
    }

    // Repositories
    single<UserRepository> {
        UserRepositoryImpl(
            apiService = get()
        )
    }

    single<FeedRepository> {
        FeedRepositoryImpl(
            apiService = get(),
            localDataSource = get()
        )
    }

    // Use Cases
    factory { GetHomeFeedUseCase(get()) }
    factory { SendOtpUseCase(get()) }
    factory { VerifyOtpUseCase(get()) }
    factory { SignInWithGoogleUseCase(get()) }

    // ViewModels
    factory {
        HomeFeedViewModel(getHomeFeedUseCase = get())
    }

    factory { SplashViewModel(
        userDataStore = get<UserDataStore>(),
        get<BookishApiService>())
    }

    factory {
        LoginViewModel(
            sendOtpUseCase = get(),
            verifyOtpUseCase = get(),
            signInWithGoogleUseCase = get(),
            loginUserUseCase = get(),
            userDataStore = get(),
        )
    }
}