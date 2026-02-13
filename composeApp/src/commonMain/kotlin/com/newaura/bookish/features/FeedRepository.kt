package com.newaura.bookish.features

import com.newaura.bookish.features.post.data.CreatePostRequest
import com.newaura.bookish.model.FeedData
import kotlinx.coroutines.flow. Flow

interface FeedRepository {
    suspend fun getHomeFeed(): Flow<Result<List<FeedData>>>
    suspend fun getFeedDetail(feedId: String): Result<FeedData?>
    suspend fun likeFeed(feedId: String): Result<Boolean>
    suspend fun createPost(createPostRequest: CreatePostRequest): Flow<Result<FeedData?>>
    suspend fun searchFeeds(query: String): Flow<Result<List<FeedData>>>
}