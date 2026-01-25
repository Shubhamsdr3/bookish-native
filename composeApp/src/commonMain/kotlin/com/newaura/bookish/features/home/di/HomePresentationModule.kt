package com.newaura.bookish.features.home.di

import com.newaura.bookish.features.feed.ui.HomeFeedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homePresentationModule = module {
    viewModel { HomeFeedViewModel(get()) }
}