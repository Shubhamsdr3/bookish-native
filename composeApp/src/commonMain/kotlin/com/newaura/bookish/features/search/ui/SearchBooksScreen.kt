package com.newaura.bookish.features.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.TextViewMedium
import com.newaura.bookish.core.ui.NetworkImage
import com.newaura.bookish.model.BookDetail
import org.koin.compose.viewmodel.koinViewModel

class SearchBooksScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinViewModel<SearchBooksViewModel>()
        val screenState by viewModel.screenState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val searchQuery = remember { mutableStateOf("") }

        Scaffold(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0xFFF5F5F5),
            topBar = {
                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    title = {
                        TextViewMedium("Search Books")
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.clickable {
                                navigator.pop()
                            }
                        )
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Search TextField
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        placeholder = {
                            TextViewBody(
                                "Search books...",
                                color = Color.LightGray
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            viewModel.searchBooks(searchQuery.value)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Search")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content based on state
                when (val state = screenState.uiState) {
                    is BookSearchUiState.Idle -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            TextViewBody(
                                "Search for books to get started",
                                color = Color.Gray
                            )
                        }
                    }

                    is BookSearchUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is BookSearchUiState.Success -> {
                        if (state.books.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                TextViewBody(
                                    "No books found",
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = state.books,
                                    key = { book -> book.id }
                                ) { book ->
                                    BookSearchResultCard(
                                        book = book,
                                        onClick = {
                                            viewModel.selectBook(book)
                                            navigator.pop()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    is BookSearchUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TextViewBody(
                                    state.message,
                                    color = Color.Red
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.searchBooks(searchQuery.value) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Blue
                                    )
                                ) {
                                    TextViewBody("Retry", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookSearchResultCard(
    book: BookDetail,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(
                onClick = onClick,
                indication = ripple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Book Thumbnail
        NetworkImage(
            url = book.volumeInfo?.imageLinks?.thumbnail ?: "",
            contentDescription = "Book Cover",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeHolder = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "No Image",
                        tint = Color.Gray
                    )
                }
            }
        )

        // Book Info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TextViewMedium(
                text = book.volumeInfo?.title ?: "Unknown Title",
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            book.volumeInfo?.authors?.firstOrNull()?.let {
                TextViewBody(
                    text = "by $it",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            book.volumeInfo?.publishedDate?.let {
                TextViewBody(
                    text = it,
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}

