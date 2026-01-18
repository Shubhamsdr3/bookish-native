package com.newaura.bookish.features.feed.domain.data

import com.newaura.bookish.model.FeedData

class FeedLocalDataSource {
    fun getCachedFeeds(): List<FeedData> {
        return listOf()
    }

    fun cacheFeeds(feeds: List<FeedData>) {
        TODO("Not yet implemented")
    }
}