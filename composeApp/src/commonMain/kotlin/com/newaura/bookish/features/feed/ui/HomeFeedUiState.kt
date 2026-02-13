package com.newaura.bookish.features.feed.ui

import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.PaginationMetadata

sealed interface HomeFeedUiState {
    data object Loading : HomeFeedUiState
    data class Success(
        val feeds: List<FeedData>,
        val paginationMetadata: PaginationMetadata = PaginationMetadata()
    ) : HomeFeedUiState
    data class Error(val message: String) : HomeFeedUiState
}

data class HomeFeedScreenState(
    val uiState: HomeFeedUiState = HomeFeedUiState.Loading,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val selectedFeed: FeedData? = null
)