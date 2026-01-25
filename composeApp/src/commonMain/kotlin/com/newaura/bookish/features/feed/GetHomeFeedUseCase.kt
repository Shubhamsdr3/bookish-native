package com.newaura.bookish.features.feed

import com.newaura.bookish.features.FeedRepository
import com.newaura.bookish.model.FeedData
import kotlinx.coroutines.flow.Flow

class GetHomeFeedUseCase(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(): Flow<Result<List<FeedData>>> {
        return feedRepository.getHomeFeed()
    }
}