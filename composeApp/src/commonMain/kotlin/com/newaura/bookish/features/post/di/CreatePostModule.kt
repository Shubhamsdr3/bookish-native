package com.newaura.bookish.features.post.di

import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.post.data.dto.ImageUploadRepositoryImpl
import com.newaura.bookish.features.post.domain.ImageUploadRepository
import com.newaura.bookish.features.post.ui.CreatePostViewModel
import com.newaura.bookish.features.post.domain.CreatePostUseCase
import com.newaura.bookish.features.post.domain.ImageUploadManager
import org.koin.dsl.module

val createPostModule = module {
    factory { CreatePostUseCase(get()) }

    factory<ImageUploadRepository> {
        ImageUploadRepositoryImpl(
            uploadManager = get(),
            uploadWorkManager = get<ImageUploadManager>()
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


