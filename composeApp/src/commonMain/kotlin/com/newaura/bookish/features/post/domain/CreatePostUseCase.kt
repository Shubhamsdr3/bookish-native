package com.newaura.bookish.features.post.domain

import com.newaura.bookish.features.FeedRepository
import com.newaura.bookish.features.post.data.dto.CreatePostRequest
import com.newaura.bookish.model.FeedResponse
import kotlinx.coroutines.flow.Flow

class CreatePostUseCase(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(request: CreatePostRequest): Flow<Result<FeedResponse?>> {
        return feedRepository.createPost(request)
    }
}