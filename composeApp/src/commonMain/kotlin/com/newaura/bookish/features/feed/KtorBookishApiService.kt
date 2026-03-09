package com.newaura.bookish.features.feed

import com.newaura.bookish.core.network.ApiResponse
import com.newaura.bookish.features.post.data.dto.CreatePostRequest
import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.FeedResponse
import com.newaura.bookish.model.FeedApiResponse
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
import io.ktor.http.content.OutgoingContent
import io.ktor.client.plugins.observer.ResponseObserver
import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.profile.data.ProfileResponse
import com.newaura.bookish.features.search.data.model.SearchResultResponse
import com.newaura.bookish.model.BookDetail
import com.newaura.bookish.model.BookDetailResponse
import io.ktor.client.request.HttpRequest

class KtorBookishApiService(initialAuthToken: String = "") : BookishApiService {

    companion object {

        const val BASE_URL = "https://20241221t151330-dot-bookish-5aae7.df.r.appspot.com"
    }

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
        install(ResponseObserver) {
            onResponse { response ->
                logCurlCommand(response.call.request)
            }
        }
        install(DefaultRequest) {
            if (authToken.isNotEmpty()) {
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }
        }
    }

    private fun logCurlCommand(request: HttpRequest) {
        val curlCommand = StringBuilder("curl -X ${request.method.value}")

        request.headers.forEach { key, values ->
            values.forEach { value ->
                curlCommand.append(" -H \"$key: ${if (key == HttpHeaders.Authorization) "Bearer $authToken" else value}\"")
                curlCommand.append(" \\\n")
            }
        }

        try {
            if (request.content is OutgoingContent.ByteArrayContent) {
                val content = (request.content as OutgoingContent.ByteArrayContent).bytes()
                val bodyString = content.decodeToString()
                if (bodyString.isNotBlank()) {
                    curlCommand.append(" -d '").append(bodyString).append("' \\\n")
                }
            } else {
                AppLogger.d("Request content type: ${request.content::class.simpleName}")
            }
        } catch (e: Exception) {
            AppLogger.e("Error extracting request body", e)
        }

        // Add URL
        curlCommand.append(" \"${request.url}\"")

        AppLogger.d("====== CURL COMMAND ======")
        AppLogger.d(curlCommand.toString())
        AppLogger.d("==========================")
    }

    override suspend fun getHomeFeed(page: Int, limit: Int): FeedApiResponse? {
        return try {
            val httpResponse = httpClient.get("$BASE_URL/api/bookish/home/feed") {
                parameter("page", page)
                parameter("offset", limit)
            }
            httpResponse.body<FeedApiResponse>()
        } catch (ex: Exception) {
            AppLogger.e("Error fetching home feed", ex)
            throw ex
        }
    }

    override suspend fun getFeedDetail(feedId: String): FeedData? {
        return httpClient.get("$BASE_URL/feed/$feedId").body()
    }

    override suspend fun likeFeed(feedId: String): Boolean {
        val response = httpClient.post("$BASE_URL/feed/$feedId/like")
        return response.status == HttpStatusCode.OK
    }

    override suspend fun createPost(createPostRequest: CreatePostRequest): FeedResponse {
        return try {
            val httpResponse = httpClient.post("$BASE_URL/api/bookish/createPost") {
                contentType(ContentType.Application.Json)
                setBody(createPostRequest)
            }
            val response = httpResponse.body<FeedResponse>()
            AppLogger.d("Create Post Response: $response")
            response
        } catch (ex: Exception) {
            AppLogger.e("Error creating post", ex)
            AppLogger.e("Full exception details: ${ex.stackTraceToString()}")
            throw ex
        }
    }

    override suspend fun searchFeeds(query: String): FeedResponse {
        return httpClient.get("$BASE_URL/feed/search") {
            parameter("q", query)
        }.body()
    }

    override suspend fun getUserProfile(userId: String): ApiResponse<ProfileResponse>? {
        return try {
            val response = httpClient.get(
                "http://192.168.0.10:5500/reading_stats.json") {
                parameter("userId", userId)
            }.body<ApiResponse<ProfileResponse>>()
            AppLogger.d("Search Books Response: $response")
            response
        } catch (ex: Exception) {
            AppLogger.e("Error searching books", ex)
            null
        }
    }

    override suspend fun getUserById(userId: String): ApiResponse<User?> {
        return httpClient.get("$BASE_URL/user/$userId").body()
    }

    override suspend fun updateProfile(user: User): ApiResponse<User?> {
        return httpClient.put("$BASE_URL/user/me") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }

    override suspend fun loginUser(user: User): Result<ApiResponse<UserResponseDto>> {
        return try {
            val response = httpClient.post("$BASE_URL/api/bookish/login") {
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

    override suspend fun searchBook(query: String): ApiResponse<SearchResultResponse>? {
        return try {
            val response = httpClient.get("$BASE_URL/api/bookish/book/search") {
                parameter("searchTerm", query)
            }.body<ApiResponse<SearchResultResponse>>()
            AppLogger.d("Search Books Response: $response")
            response
        } catch (ex: Exception) {
            AppLogger.e("Error searching books", ex)
            null
        }
    }

    override suspend fun fetchBookDetail(bookId: String): ApiResponse<BookDetailResponse>? {
        return try {
            val response = httpClient.get("$BASE_URL/api/bookish/book") {
                parameter("id", bookId)
            }.body<ApiResponse<BookDetailResponse>>()
            AppLogger.d("Book detail response: $response")
            response
        } catch (ex: Exception) {
            AppLogger.e("Error searching books", ex)
            null
        }
    }
}