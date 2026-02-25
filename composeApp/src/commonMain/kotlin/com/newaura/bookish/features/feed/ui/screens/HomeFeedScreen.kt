package com.newaura.bookish.features.feed.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.TextViewMedium
import com.newaura.bookish.features.feed.ui.HomeFeedScreenState
import com.newaura.bookish.features.feed.ui.HomeFeedUiState
import com.newaura.bookish.features.feed.ui.HomeFeedViewModel
import com.newaura.bookish.features.mybooks.MyBookScreen
import com.newaura.bookish.features.search.ui.SearchBooksScreen
import com.newaura.bookish.model.FeedData
import org.koin.compose.viewmodel.koinViewModel

class HomeFeedScreen() : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: HomeFeedViewModel = koinViewModel<HomeFeedViewModel>()
        val screenState by viewModel.screenState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            topBar = {
                TopAppBar(
                    title = {
                        TextViewMedium("Bookish")
                    },
                    actions = {
                        IconButton(onClick = {

                        }) {
                            // Profile icon
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TextViewBody("What are you reading today ?")
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                        .height(50.dp)
                        .clickable {
                            navigator.push(SearchBooksScreen())
                        }
                        .drawBehind {
                            val strokeWith = 1.dp.toPx()
                            drawRoundRect(
                                color = Color.Gray,
                                style = Stroke(
                                    width = strokeWith,
                                    pathEffect = PathEffect.dashPathEffect(
                                        intervals = floatArrayOf(8f, 8f),
                                        phase = 0f
                                    ),
                                ),
                                cornerRadius = CornerRadius(12.dp.toPx())
                            )
                        }.background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = "Add post",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
                        )
                        TextViewBody(
                            text = "Share a quote or thought",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                HomeFeedContent(
                    screenState = screenState,
                    onFeedClick = {

                    },
                    onRetry = viewModel::loadFeed,
                    onRefresh = viewModel::refresh,
                    onLoadMore = viewModel::loadMore,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.currentOrThrow

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
            val lazyListState = rememberLazyListState()

            val shouldLoadMore by remember {
                derivedStateOf {
                    val layoutInfo = lazyListState.layoutInfo
                    val totalItems = layoutInfo.totalItemsCount
                    val visibleItems = layoutInfo.visibleItemsInfo.size
                    val lastVisibleIndex = if (visibleItems > 0) {
                        layoutInfo.visibleItemsInfo.last().index
                    } else {
                        0
                    }
                    lastVisibleIndex >= totalItems - 3 && uiState.paginationMetadata.hasNextPage
                }
            }

            LaunchedEffect(shouldLoadMore) {
                if (shouldLoadMore && !screenState.isLoadingMore) {
                    onLoadMore()
                }
            }

            PullToRefreshBox(
                isRefreshing = screenState.isRefreshing,
                onRefresh = onRefresh,
                state = pullRefreshState,
                modifier = modifier
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = lazyListState
                ) {
                    items(
                        items = uiState.feeds,
                        key = { feed -> feed.post?.id ?: feed.fallbackId }
                    ) { feed ->
                        FeedCard(
                            feedData = feed,
                            onClick = { onFeedClick(feed) },
                            onBookNameClick = { _ ->
                                // Navigate to book detail.
                                navigator.push(MyBookScreen())
                            }
                        )
                    }

                    if (screenState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
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