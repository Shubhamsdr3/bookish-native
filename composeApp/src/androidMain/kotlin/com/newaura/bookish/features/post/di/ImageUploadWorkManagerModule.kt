package com.newaura.bookish.features.post.di

import com.newaura.bookish.features.post.data.ImageUploadWorkManager
import com.newaura.bookish.features.post.domain.ImageUploadManager
import com.newaura.bookish.features.post.util.ImageCompressionUtil
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module for WorkManager-based image upload dependencies
 * Simple and straightforward - no custom factory needed
 */
val imageUploadWorkManagerModule = module {
    single<ImageCompressionUtil> {
        ImageCompressionUtil(androidContext())
    }

    single<ImageUploadManager> {
        ImageUploadWorkManager(
            context = androidContext()
        )
    }
}


