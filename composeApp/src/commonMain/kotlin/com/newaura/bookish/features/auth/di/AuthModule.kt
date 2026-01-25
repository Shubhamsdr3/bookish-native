package com.newaura.bookish.features.auth.di

import com.newaura.bookish.core.Context
import com.newaura.bookish.features.auth.data.AuthRepositoryImpl
import com.newaura.bookish.features.auth.domain.AuthRepository
import com.newaura.bookish.features.auth.domain.PhoneAuthService
import com.newaura.bookish.features.feed.BookishApiService
import com.newaura.bookish.features.feed.LoginUserUseCase
import com.newaura.bookish.features.feed.SendOtpUseCase
import com.newaura.bookish.features.feed.VerifyOtpUseCase
import org.koin.dsl.module

val authDataModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(
            get<PhoneAuthService>(),
            get<BookishApiService>()
        )
    }
}

val authDomainModule = module {
    factory { SendOtpUseCase(get<AuthRepository>()) }
    factory { VerifyOtpUseCase(get<AuthRepository>()) }
    factory { LoginUserUseCase(get<AuthRepository>()) }
}

expect fun createAuthService(context: Context): PhoneAuthService