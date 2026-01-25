package com.newaura.bookish.features.feed.di

import com.newaura.bookish.features.FeedRepository
import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.features.feed.FeedRepositoryImpl
import com.newaura.bookish.features.feed.GetHomeFeedUseCase
import com.newaura.bookish.features.feed.KtorBookishApiService
import com.newaura.bookish.features.feed.SendOtpUseCase
import com.newaura.bookish.features.feed.SignInWithGoogleUseCase
import com.newaura.bookish.features.feed.VerifyOtpUseCase
import com.newaura.bookish.features.feed.ui.HomeFeedViewModel
import com.newaura.bookish.features.feed.ui.LoginViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.FrameType.Companion.get
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.viewmodel.scope.viewModelScope


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

    factory {
        LoginViewModel(
            sendOtpUseCase = get(),
            verifyOtpUseCase = get(),
            signInWithGoogleUseCase = get(),
            loginUserUseCase = get(),
            appDataStoreRepository = get(),
        )
    }
}

//expect fun platformModule(): Module