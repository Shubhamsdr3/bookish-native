package com.newaura.bookish.features.feed.domain

import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.FeedResponse
import com.newaura.bookish.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class KtorBookishApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : BookishApiService {

    override suspend fun getHomeFeed(page: Int, limit:  Int): FeedResponse {
        return httpClient.get("$baseUrl/feed") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    override suspend fun getFeedDetail(feedId:  String): FeedData? {
        return httpClient.get("$baseUrl/feed/$feedId").body()
    }

    override suspend fun likeFeed(feedId: String): Boolean {
        val response = httpClient.post("$baseUrl/feed/$feedId/like")
        return response.status == HttpStatusCode.OK
    }

    override suspend fun createPost(caption: String, images: List<String>): FeedData {
        return httpClient.post("$baseUrl/feed") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("caption" to caption, "images" to images))
        }.body()
    }

    override suspend fun searchFeeds(query: String): FeedResponse {
        return httpClient.get("$baseUrl/feed/search") {
            parameter("q", query)
        }.body()
    }

    override suspend fun getCurrentUser(): User? {
        return httpClient.get("$baseUrl/user/me").body()
    }

    override suspend fun getUserById(userId:  String): User? {
        return httpClient.get("$baseUrl/user/$userId").body()
    }

    override suspend fun updateProfile(user: User): User {
        return httpClient.put("$baseUrl/user/me") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }
}