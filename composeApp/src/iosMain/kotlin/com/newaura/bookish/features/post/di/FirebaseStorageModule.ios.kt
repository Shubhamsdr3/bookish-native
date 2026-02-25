package com.newaura.bookish.features.post.di

import com.newaura.bookish.features.post.domain.FirebaseStorageService
import com.newaura.bookish.features.post.domain.FirebaseStorageServiceImpl
import org.koin.dsl.module

/**
 * iOS-specific Koin module for Firebase Storage
 * Provides FirebaseStorageService implementation for iOS platform
 */
val firebaseStorageModule = module {
    single<FirebaseStorageService> {
        FirebaseStorageServiceImpl()
    }
}

