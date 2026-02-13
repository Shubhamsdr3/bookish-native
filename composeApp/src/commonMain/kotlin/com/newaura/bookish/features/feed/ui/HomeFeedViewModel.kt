package com.newaura.bookish.features.feed.ui

import androidx.lifecycle.ViewModel
import com.newaura.bookish.features.feed.domain.GetHomeFeedUseCase
import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.PaginationMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeFeedViewModel(
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
    coroutineScope: CoroutineScope? = null
) : ViewModel() {
    private val viewModelScope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _screenState = MutableStateFlow(HomeFeedScreenState())
    val screenState: StateFlow<HomeFeedScreenState> = _screenState.asStateFlow()

    private var currentPage = 1
    private var paginationMetadata = PaginationMetadata()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _screenState.update { it.copy(uiState = HomeFeedUiState.Loading) }
            getHomeFeedUseCase(page = currentPage).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        paginationMetadata = response.getPaginationMetadata()
                        _screenState.update {
                            it.copy(
                                uiState = HomeFeedUiState.Success(
                                    feeds = response.data,
                                    paginationMetadata = paginationMetadata
                                ),
                                isRefreshing = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _screenState.update {
                            it.copy(
                                uiState = HomeFeedUiState.Error(
                                    error.message ?: "Failed to load feed"
                                ),
                                isRefreshing = false
                            )
                        }
                    }
                )
            }
        }
    }

    fun loadMore() {
        if (!paginationMetadata.hasNextPage || _screenState.value.isLoadingMore) {
            return
        }
        currentPage++
        viewModelScope.launch {
            _screenState.update { it.copy(isLoadingMore = true) }
            getHomeFeedUseCase(page = currentPage).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        paginationMetadata = response.getPaginationMetadata()
                        val currentState = _screenState.value.uiState
                        if (currentState is HomeFeedUiState.Success) {
                            val combinedFeeds = currentState.feeds + response.data
                            _screenState.update {
                                it.copy(
                                    uiState = HomeFeedUiState.Success(
                                        feeds = combinedFeeds,
                                        paginationMetadata = paginationMetadata
                                    ),
                                    isLoadingMore = false
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        // Keep showing existing feeds on error, just stop loading more
                        _screenState.update { it.copy(isLoadingMore = false) }
                    }
                )
            }
        }
    }

    fun refresh() {
        _screenState.update { it.copy(isRefreshing = true) }
        loadFeed()
    }

    fun selectFeed(feed: FeedData?) {
        _screenState.update { it.copy(selectedFeed = feed) }
    }

    fun onDispose() {
        // Cleanup if needed
    }
}