package com.newaura.bookish.features.feed

import com.newaura.bookish.core.network.ApiResponse
import com.newaura.bookish.features.post.data.CreatePostRequest
import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.FeedResponse
import com.newaura.bookish.model.User
import com.newaura.bookish.model.UserResponseDto

interface BookishApiService {
    suspend fun getHomeFeed(page: Int, limit: Int): ApiResponse<List<FeedData>>?
    suspend fun getFeedDetail(feedId: String): FeedData?
    suspend fun likeFeed(feedId: String): Boolean
    suspend fun createPost(createPostRequest: CreatePostRequest): ApiResponse<FeedData>?
    suspend fun searchFeeds(query: String): FeedResponse
    suspend fun getCurrentUser(): ApiResponse<User?>
    suspend fun getUserById(userId: String): ApiResponse<User?>
    suspend fun updateProfile(user: User): ApiResponse<User?>
    suspend fun loginUser(user: User): Result<ApiResponse<UserResponseDto>>
    suspend fun setAuthToken(authToken: String)
}