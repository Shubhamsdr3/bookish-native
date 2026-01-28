package com.newaura.bookish.features.feed

import com.newaura.bookish.features.FeedRepository
import com.newaura.bookish.features.feed.data.FeedLocalDataSource
import com.newaura.bookish.model.FeedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FeedRepositoryImpl(
    private val apiService: BookishApiService,
    private val localDataSource: FeedLocalDataSource
) : FeedRepository {

    override suspend fun getHomeFeed(): Flow<Result<List<FeedData>>> = flow {
        try {
            // Emit cached data first
            val cachedFeeds = localDataSource.getCachedFeeds()
            if (cachedFeeds.isNotEmpty()) {
                emit(Result.success(cachedFeeds))
            }

            // Fetch from remote
            val response = apiService.getHomeFeed(page = 1, limit = 10)
            val feeds = response?.data ?: emptyList()
            
            // Cache the data
            localDataSource.cacheFeeds(feeds)
            
            emit(Result.success(feeds))
        } catch (e:  Exception) {
            val cached = localDataSource.getCachedFeeds()
            if (cached.isNotEmpty()) {
                emit(Result.success(cached))
            } else {
                emit(Result.failure(e))
            }
        }
    }

    override suspend fun getFeedDetail(feedId: String): Result<FeedData?> {
        return try {
            val feed = apiService.getFeedDetail(feedId)
            Result.success(feed)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likeFeed(feedId: String): Result<Boolean> {
        return try {
            val success = apiService.likeFeed(feedId)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPost(caption: String, images: List<String>): Flow<Result<FeedData>> = flow {
        try {
            val feed = apiService.createPost(caption, images)
            emit(Result.success(feed))
        } catch (e:  Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun searchFeeds(query: String): Flow<Result<List<FeedData>>> = flow {
        try {
            val response = apiService.searchFeeds(query)
            emit(Result.success(response.data ?: emptyList()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}