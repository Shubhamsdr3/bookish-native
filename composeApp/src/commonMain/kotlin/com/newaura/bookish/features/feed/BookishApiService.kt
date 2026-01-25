package com.newaura.bookish.features.feed

import com.newaura.bookish.core.network.ApiResponse
import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.FeedResponse
import com.newaura.bookish.model.User
import com.newaura.bookish.model.UserResponseDto

interface BookishApiService {
    suspend fun getHomeFeed(page: Int, limit: Int): ApiResponse<List<FeedData>>?
    suspend fun getFeedDetail(feedId: String): FeedData?
    suspend fun likeFeed(feedId: String): Boolean
    suspend fun createPost(caption: String, images: List<String>): FeedData
    suspend fun searchFeeds(query: String): FeedResponse
    suspend fun getCurrentUser(): User?
    suspend fun getUserById(userId: String): User?
    suspend fun updateProfile(user: User): User
    suspend fun loginUser(user: User): Result<ApiResponse<UserResponseDto>>
    suspend fun setAuthToken(authToken: String)
}