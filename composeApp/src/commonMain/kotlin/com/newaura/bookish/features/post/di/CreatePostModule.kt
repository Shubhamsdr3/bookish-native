package com.newaura.bookish.features.post.di

import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.post.data.ImageUploadRepository
import com.newaura.bookish.features.post.data.ImageUploadRepositoryImpl
import com.newaura.bookish.features.post.ui.CreatePostViewModel
import com.newaura.bookish.features.post.domain.CreatePostUseCase
import com.newaura.bookish.features.post.domain.ImageUploadManager
import org.koin.dsl.module

val createPostModule = module {
    factory { CreatePostUseCase(get()) }

    // Image Upload Repository with both Firebase Storage and ImageUploadManager
    factory<ImageUploadRepository> {
        ImageUploadRepositoryImpl(
            uploadManager = get(),  // FirebaseStorageService
            uploadWorkManager = get<ImageUploadManager>()  // Platform-specific ImageUploadManager
        )
    }

    factory {
        CreatePostViewModel(
            get<CreatePostUseCase>(),
            get<UserDataStore>(),
            get<ImageUploadRepository>()
        )
    }
}


