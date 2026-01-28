package com.newaura.bookish.features.post.di

import com.newaura.bookish.features.post.CreatePostViewModel
import com.newaura.bookish.features.post.domain.CreatePostUseCase
import org.koin.dsl.module

val createPostModule = module {
    factory { CreatePostUseCase(get()) }
    factory { CreatePostViewModel(get<CreatePostUseCase>()) }
}
