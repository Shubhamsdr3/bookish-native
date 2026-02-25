package com.newaura.bookish.features.post.di

import com.google.firebase.storage.FirebaseStorage
import com.newaura.bookish.features.post.domain.FirebaseStorageService
import com.newaura.bookish.features.post.domain.FirebaseStorageServiceImpl
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module for Firebase Storage
 * Provides FirebaseStorageService implementation for Android platform
 */
val firebaseStorageModule = module {
    single<FirebaseStorage> {
        FirebaseStorage.getInstance()
    }

    single<FirebaseStorageService> {
        FirebaseStorageServiceImpl(
            context = androidContext(),
            storage = get()
        )
    }
}

