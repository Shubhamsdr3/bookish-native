package com.newaura.bookish.features.bookdetail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.core.common.HtmlTextView
import com.newaura.bookish.core.common.ReadingProgressCard
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.TextViewLight
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
    val volumeInfo = bookDetail.volumeInfo
    Column(
        modifier = modifier.padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            NetworkImage(
                url = volumeInfo?.imageLinks?.getSecureImageMedium() ?: "",
                contentDescription = volumeInfo?.title ?: "",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.FillBounds,
            )
        }
        Spacer(Modifier.height(24.dp))
        TextViewBody(
            text = volumeInfo?.title ?: "",
        )
        Spacer(Modifier.height(16.dp))
        TextViewMedium(
            text = getAuthor(volumeInfo?.authors),
        )
        Spacer(Modifier.height(16.dp))
        RatingWidget(
            volumeInfo?.avgRating ?: 0.0,
            volumeInfo?.ratingCount ?: 0
        )
        volumeInfo?.categories?.let { categories ->
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    Button(
                        colors = ButtonColors(
                            containerColor = Color(0xFF62BCA3),
                            contentColor = Color(0xFF02805D),
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContentColor = MaterialTheme.colorScheme.surface
                        ),
                        onClick = { /* Handle category click */ },
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        TextViewBody(text = category.trim())
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        volumeInfo?.readingProgress?.let {
            ReadingProgressCard(0.65f, modifier)

            Spacer(Modifier.height(24.dp))
        }
        volumeInfo?.reviews?.let {
            Column {
                TextViewBody("Reviews (2)")
                Spacer(Modifier.height(16.dp))
                ReviewCard()
                ReviewCard()
                ReviewCard()
            }
            Spacer(Modifier.height(24.dp))
        }
        volumeInfo?.description?.let {
            HtmlTextView(it)
        }
    }
}

@Composable
fun ReviewCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = Color(0xFFE1E8E6),
        )
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                NetworkImage(
                    url = "",//TODO add reviews
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeHolder = {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profile placeholder",
                        )
                    }
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    TextViewMedium("Sara Chen")
                    TextViewBody("2 days ago")
                }
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        tint = Color(0xFFEED338),
                        contentDescription = "Rating"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TextViewBody("4.5")
                }
            }
            Spacer(Modifier.height(16.dp))
            TextViewBody(text = "The Midnight Library is a beautiful exploration of life\\'s infinite possibilities. Matt Haig\\'s writing is both profound and accessible, making you think deeply about your own choices while keeping you thoroughly entertained.")
            Spacer(Modifier.height(16.dp))
            Row {
                Icon(Icons.Default.Favorite, contentDescription = "")
                Spacer(Modifier.width(4.dp))
                TextViewBody("18 helpful")
            }
        }
    }
}

@Composable
private fun RatingWidget(avgRating: Double, ratingCount: Int) {
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
                    tint = Color(0xFFE3AD44)
                )
            }
        }
        TextViewMedium(
            text = "$avgRating"
        )
        TextViewBody(
            text = "($ratingCount reviews)",
            color = Color(0xFF4A5565)
        )
    }
}

private fun getAuthor(authors: List<String>?): String {
    return authors?.joinToString(", ") ?: ""
}