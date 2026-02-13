package com.newaura.bookish.features.feed

import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.FeedRepository
import com.newaura.bookish.features.feed.data.FeedLocalDataSource
import com.newaura.bookish.features.post.data.CreatePostRequest
import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.FeedApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FeedRepositoryImpl(
    private val apiService: BookishApiService,
    private val localDataSource: FeedLocalDataSource
) : FeedRepository {

    override suspend fun getHomeFeed(page: Int, limit: Int): Flow<Result<FeedApiResponse>> = flow {
        try {
            if (page == 1) {
                val cachedFeeds = localDataSource.getCachedFeeds()
                if (cachedFeeds.isNotEmpty()) {
                    val cachedResponse = FeedApiResponse(
                        isSuccess = true,
                        data = cachedFeeds
                    )
                    emit(Result.success(cachedResponse))
                }
            }

            val response = apiService.getHomeFeed(page = page, limit = limit)

            if (response != null && response.isSuccess) {
                if (page == 1 && response.data.isNotEmpty()) {
                    localDataSource.cacheFeeds(response.data)
                }
                emit(Result.success(response))
            } else {
                emit(Result.failure(Exception("Failed to fetch feed")))
            }
        } catch (e: Exception) {
            val cached = localDataSource.getCachedFeeds()
            if (page == 1 && cached.isNotEmpty()) {
                val cachedResponse = FeedApiResponse(
                    isSuccess = true,
                    data = cached
                )
                emit(Result.success(cachedResponse))
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

    override suspend fun createPost(createPostRequest: CreatePostRequest): Flow<Result<FeedData?>> = flow {
        try {
            val response = apiService.createPost(createPostRequest)
            if (response?.isSuccess == true && response.data != null) {
                AppLogger.d("Create post successful: ${response.data}")
                emit(Result.success(response.data))
            } else {
                AppLogger.e("Create post failed: ${response?.message}")
                emit(Result.failure(Exception(response?.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            AppLogger.e("Error creating post", e)
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