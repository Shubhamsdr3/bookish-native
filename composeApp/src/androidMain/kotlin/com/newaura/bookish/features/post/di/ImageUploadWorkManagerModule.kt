package com.newaura.bookish.features.post.di

import com.newaura.bookish.features.post.data.ImageUploadWorkManager
import com.newaura.bookish.features.post.domain.ImageUploadManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module for WorkManager-based image upload dependencies
 * Only loaded for Android platform
 * Provides the platform-specific ImageUploadWorkManager as the ImageUploadManager interface
 */
val imageUploadWorkManagerModule = module {
    single<ImageUploadManager> {
        ImageUploadWorkManager(
            context = androidContext()
        )
    }
}


