package com.newaura.bookish.features.post.di

import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.post.data.ImageUploadRepository
import com.newaura.bookish.features.post.data.ImageUploadRepositoryImpl
import com.newaura.bookish.features.post.ui.CreatePostViewModel
import com.newaura.bookish.features.post.domain.CreatePostUseCase
import org.koin.dsl.module

val createPostModule = module {
    factory { CreatePostUseCase(get()) }

    // Image Upload Repository
    factory<ImageUploadRepository> {
        ImageUploadRepositoryImpl(
            firebaseStorageService = get()
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
