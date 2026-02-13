package com.newaura.bookish.features.feed.domain

import com.newaura.bookish.features.FeedRepository
import com.newaura.bookish.model.FeedApiResponse
import kotlinx.coroutines.flow.Flow

class GetHomeFeedUseCase(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 10): Flow<Result<FeedApiResponse>> {
        return feedRepository.getHomeFeed(page = page, limit = limit)
    }
}