package com.newaura.bookish.features.feed.di

import com.newaura.bookish.features.feed.data.FeedLocalDataSource
import com.newaura.bookish.features.feed.data.FeedLocalDataSourceImpl
import org.koin.dsl.module

val feedDataModule = module {
    single<FeedLocalDataSource> { FeedLocalDataSourceImpl() }
}
