package com.newaura.bookish.features.feed.ui

import androidx.lifecycle.ViewModel
import com.newaura.bookish.features.feed.domain.GetHomeFeedUseCase
import com.newaura.bookish.model.FeedData
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
    coroutineScope: CoroutineScope?  = null
): ViewModel() {
    private val viewModelScope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _screenState = MutableStateFlow(HomeFeedScreenState())
    val screenState: StateFlow<HomeFeedScreenState> = _screenState.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _screenState.update { it.copy(uiState = HomeFeedUiState.Loading) }

            getHomeFeedUseCase().collect { result ->
                result.fold(
                    onSuccess = { feeds ->
                        _screenState.update {
                            it.copy(
                                uiState = HomeFeedUiState.Success(feeds),
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