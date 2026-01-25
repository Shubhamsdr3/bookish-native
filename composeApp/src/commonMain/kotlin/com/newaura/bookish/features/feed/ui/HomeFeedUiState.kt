package com.newaura.bookish.features.feed.ui

import com.newaura.bookish.model.FeedData

sealed interface HomeFeedUiState {
    data object Loading : HomeFeedUiState
    data class Success(val feeds: List<FeedData>) : HomeFeedUiState
    data class Error(val message: String) : HomeFeedUiState
}

data class HomeFeedScreenState(
    val uiState: HomeFeedUiState = HomeFeedUiState.Loading,
    val isRefreshing: Boolean = false,
    val selectedFeed: FeedData? = null
)