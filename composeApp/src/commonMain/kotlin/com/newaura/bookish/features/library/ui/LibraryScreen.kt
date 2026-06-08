package com.newaura.bookish.features.library.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.TextViewMedium
import com.newaura.bookish.features.library.data.LibraryBook
import org.koin.compose.viewmodel.koinViewModel

class LibraryScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel = koinViewModel<LibraryViewModel>()

        val libraryUiState = viewModel.libraryUiScreenState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.fetchBooks()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TextViewMedium("My Library")
                    }
                )
            }
        ) { paddingValues ->
            when (val uiState = libraryUiState.value) {
                is LibraryScreenState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is LibraryScreenState.OnError -> {
                    Text(text = uiState.errorMessage)
                }
                is LibraryScreenState.OnSuccess -> {
                    LibraryContent(
                        Modifier.fillMaxSize().padding(paddingValues),
                        books = uiState.books
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun LibraryContent(modifier: Modifier, books: List<LibraryBook>) {
    Column(modifier = modifier) {
        TextViewBody("Currently reading")

    }
}