package com.newaura.bookish.features.feed.domain

import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.FeedResponse
import com.newaura.bookish.model.User

interface BookishApiService {
    suspend fun getHomeFeed(page: Int, limit: Int): FeedResponse
    suspend fun getFeedDetail(feedId: String): FeedData?
    suspend fun likeFeed(feedId: String): Boolean
    suspend fun createPost(caption: String, images: List<String>): FeedData
    suspend fun searchFeeds(query: String): FeedResponse
    suspend fun getCurrentUser(): User?
    suspend fun getUserById(userId: String): User?
    suspend fun updateProfile(user: User): User
}