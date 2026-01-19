package com.newaura.bookish.features.feed.domain.di

import com.newaura.bookish.features.feed.domain.data.FeedLocalDataSource
import com.newaura.bookish.features.feed.domain.data.FeedLocalDataSourceImpl
import org.koin.dsl.module

val feedDataModule = module {
    single<FeedLocalDataSource> { FeedLocalDataSourceImpl() }
}
