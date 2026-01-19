package com.newaura.bookish.features.feed.domain.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import cafe.adriel.voyager.core.screen.Screen
import com.newaura.bookish.features.feed.domain.ui.HomeFeedScreenState
import com.newaura.bookish.features.feed.domain.ui.HomeFeedUiState
import com.newaura.bookish.features.feed.domain.ui.HomeFeedViewModel
import com.newaura.bookish.model.FeedData

class HomeFeedScreen(
//    private val onFeedClick: (FeedData) -> Unit,
//    private val onProfileClick: () -> Unit,
//    private val onCreatePostClick: () -> Unit
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: HomeFeedViewModel = koinViewModel<HomeFeedViewModel>()
        val screenState by viewModel.screenState.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Bookish") },
                    actions = {
                        IconButton(onClick = {

                        }) {
                            // Profile icon
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {

                }) {
                    // Add icon
                }
            }
        ) { paddingValues ->
            HomeFeedContent(
                screenState = screenState,
                onFeedClick = {

                },
                onRetry = viewModel::loadFeed,
                onRefresh = viewModel::refresh,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeFeedContent(
    screenState: HomeFeedScreenState,
    onFeedClick: (FeedData) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (val uiState = screenState.uiState) {
        is HomeFeedUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is HomeFeedUiState.Success -> {
            val pullRefreshState = rememberPullToRefreshState()

            PullToRefreshBox(
                isRefreshing = screenState.isRefreshing,
                onRefresh = onRefresh,
                state = pullRefreshState,
                modifier = modifier
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.feeds,
                        key = { "${it.user?.phoneNumber}_${it.post?.caption?.take(20)}" }
                    ) { feed ->
                        FeedCard(
                            feedData = feed,
                            onClick = { onFeedClick(feed) }
                        )
                    }
                }
            }
        }

        is HomeFeedUiState.Error -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun FeedCard(feedData: FeedData, onClick: () -> Unit) {
    TODO("Not yet implemented")
}