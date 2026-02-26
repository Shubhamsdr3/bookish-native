package com.newaura.bookish.features.post.di

import com.newaura.bookish.features.post.data.NoOpImageUploadManager
import com.newaura.bookish.features.post.domain.ImageUploadManager
import org.koin.dsl.module

/**
 * Koin module for image upload dependencies
 * Provides default no-op implementation that can be overridden on specific platforms
 *
 * Android platform will override this with ImageUploadWorkManager via imageUploadWorkManagerModule
 */
val imageUploadModule = module {
    single<ImageUploadManager> {
        NoOpImageUploadManager()
    }
}

