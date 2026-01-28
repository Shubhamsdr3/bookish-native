package com.newaura.bookish.features.post.domain

import com.newaura.bookish.features.FeedRepository
import com.newaura.bookish.model.FeedData
import kotlinx.coroutines.flow.Flow

class CreatePostUseCase(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(caption: String, images: List<String>): Flow<Result<FeedData>> {
        return feedRepository.createPost(caption, images)
    }
}