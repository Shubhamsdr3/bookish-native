package com.newaura.bookish.features.feed

import com.newaura.bookish.core.network.ApiResponse
import com.newaura.bookish.features.post.data.dto.CreatePostRequest
import com.newaura.bookish.features.profile.data.ProfileResponse
import com.newaura.bookish.features.search.data.model.SearchResultResponse
import com.newaura.bookish.model.BookDetail
import com.newaura.bookish.model.BookDetailResponse
import com.newaura.bookish.model.FeedApiResponse
import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.FeedResponse
import com.newaura.bookish.model.User
import com.newaura.bookish.model.UserResponseDto

interface BookishApiService {
    suspend fun getHomeFeed(page: Int, limit: Int): FeedApiResponse?
    suspend fun getFeedDetail(feedId: String): FeedData?
    suspend fun likeFeed(feedId: String): Boolean
    suspend fun createPost(createPostRequest: CreatePostRequest): FeedResponse
    suspend fun searchFeeds(query: String): FeedResponse
    suspend fun getUserProfile(userId: String): ApiResponse<ProfileResponse>?
    suspend fun getUserById(userId: String): ApiResponse<User?>
    suspend fun updateProfile(user: User): ApiResponse<User?>
    suspend fun loginUser(user: User): Result<ApiResponse<UserResponseDto>>
    suspend fun setAuthToken(authToken: String)

    suspend fun searchBook(query: String): ApiResponse<SearchResultResponse>?

    suspend fun fetchBookDetail(bookId: String): ApiResponse<BookDetailResponse>?
}