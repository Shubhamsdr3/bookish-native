package com.newaura.bookish.features.bookdetail.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.TextViewMedium
import com.newaura.bookish.core.ui.NetworkImage
import com.newaura.bookish.model.BookDetail
import org.koin.compose.viewmodel.koinViewModel

class MyBookScreen(private val bookId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val viewModel: BooksViewModel = koinViewModel<BooksViewModel>()

        val uiState = viewModel.bookDetailUiState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getBookDetail(bookId)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { TextViewBody("Back") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier.fillMaxSize()
                        .padding(paddingValues)

            ) {
                when (val state = uiState.value) {
                    is BookDetailUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is BookDetailUiState.Success -> {
                        BookDetailContent(
                            modifier = Modifier.fillMaxSize(),
                            bookDetail = state.bookDetail,
                        )
                    }

                    is BookDetailUiState.Error -> {
                        Text(text = state.message)
                    }

                    else -> {
                        // do nothing
                    }
                }
            }

        }
    }
}

@Composable
private fun BookDetailContent(modifier: Modifier, bookDetail: BookDetail) {
    Column(
        modifier = modifier.padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            NetworkImage(
                url = bookDetail.volumeInfo?.imageLinks?.getSecureThumbnail() ?: "",
                contentDescription = bookDetail.volumeInfo?.title ?: "",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.FillBounds,
            )
        }
        Spacer(Modifier.height(24.dp))
        TextViewBody(
            text = bookDetail.volumeInfo?.title ?: "",
        )
        Spacer(Modifier.height(16.dp))
        TextViewMedium(
            text = getAuthor(bookDetail.volumeInfo?.authors),
        )
        Spacer(Modifier.height(16.dp))
        RatingWidget(
            bookDetail.volumeInfo?.avgRating ?: 0.0,
            bookDetail.volumeInfo?.ratingCount ?: 0.0
        )
        Spacer(Modifier.height(16.dp))
        TextViewBody(
            text = "${bookDetail.volumeInfo?.categories ?: 0}",
        )
        Spacer(Modifier.height(16.dp))
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                TextViewBody(
                    text = bookDetail.volumeInfo?.description ?: "",
                )
            }
        }
    }
}

@Composable
private fun RatingWidget(avgRating: Double, ratingCount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            repeat(5) { index ->
                val rating = avgRating.toInt()
                Icon(
                    imageVector = when {
                        index < rating -> Icons.Filled.Star
                        else -> Icons.Outlined.Star
                    },
                    contentDescription = "Star",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Rating text
        TextViewBody(
            text = "$avgRating"
        )

        // Rating count
        TextViewMedium(
            text = "($ratingCount ratings)"
        )
    }
}

private fun getAuthor(authors: List<String>?): String {
    return authors?.joinToString(", ") ?: ""
}