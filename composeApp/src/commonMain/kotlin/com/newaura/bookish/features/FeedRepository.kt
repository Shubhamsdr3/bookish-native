package com.newaura.bookish.features

import com.newaura.bookish.features.post.data.dto.CreatePostRequest
import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.FeedApiResponse
import com.newaura.bookish.model.FeedResponse
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun getHomeFeed(page: Int = 1, limit: Int = 10): Flow<Result<FeedApiResponse>>
    suspend fun getFeedDetail(feedId: String): Result<FeedData?>
    suspend fun likeFeed(feedId: String): Result<Boolean>
    suspend fun createPost(createPostRequest: CreatePostRequest): Flow<Result<FeedResponse?>>
    suspend fun searchFeeds(query: String): Flow<Result<List<FeedData>>>
}