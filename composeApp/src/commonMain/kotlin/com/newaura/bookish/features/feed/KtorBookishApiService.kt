package com.newaura.bookish.features.feed

import com.newaura.bookish.core.network.ApiResponse
import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.FeedResponse
import com.newaura.bookish.model.User
import com.newaura.bookish.model.UserResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorBookishApiService(initialAuthToken: String = "") : BookishApiService {

    private var authToken: String = initialAuthToken

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
        install(DefaultRequest) {
            if (authToken.isNotEmpty()) {
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }
        }
    }

    private val baseUrl = "https://20241221t151330-dot-bookish-5aae7.df.r.appspot.com"

  override suspend fun getHomeFeed(page: Int, limit: Int): ApiResponse<List<FeedData>>? {
      var response: ApiResponse<List<FeedData>>? = null
      try {
          response = httpClient.get("$baseUrl/api/bookish/home/feed") {
              parameter("page", page)
              parameter("offset", limit)
          }.body<ApiResponse<List<FeedData>>>()
      } catch (ex: Exception) {
          print(ex)
      }
      return response
  }

    override suspend fun getFeedDetail(feedId: String): FeedData? {
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

    override suspend fun getUserById(userId: String): User? {
        return httpClient.get("$baseUrl/user/$userId").body()
    }

    override suspend fun updateProfile(user: User): User {
        return httpClient.put("$baseUrl/user/me") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }

    override suspend fun loginUser(user: User): Result<ApiResponse<UserResponseDto>> {
        return try {
            val response = httpClient.post("$baseUrl/api/bookish/login") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }.body<ApiResponse<UserResponseDto>>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setAuthToken(authToken: String) {
        this.authToken = authToken
    }
}