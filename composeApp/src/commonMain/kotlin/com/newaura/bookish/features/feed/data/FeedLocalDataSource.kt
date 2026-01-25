package com.newaura.bookish.features.feed.data

import com.newaura.bookish.model.FeedData

interface FeedLocalDataSource {
    suspend fun getCachedFeeds(): List<FeedData>

    suspend fun cacheFeeds(feeds: List<FeedData>);
}


class FeedLocalDataSourceImpl: FeedLocalDataSource {
    override suspend fun getCachedFeeds(): List<FeedData> {
        return  emptyList()
    }

    override suspend fun cacheFeeds(feeds: List<FeedData>) {
        // do nothing.
    }
}
