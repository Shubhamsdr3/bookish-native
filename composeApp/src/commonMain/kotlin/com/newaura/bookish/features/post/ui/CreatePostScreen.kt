package com.newaura.bookish.features.post.ui

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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.TextViewMedium
import com.newaura.bookish.core.util.toCamelCase
import com.newaura.bookish.features.post.CreatePostViewModel
import com.newaura.bookish.features.search.ui.SearchBooksScreen
import com.newaura.bookish.model.PostType
import org.koin.compose.koinInject

class CreatePostScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    override fun Content() {
        val viewModel = koinInject<CreatePostViewModel>()
        val postScreenState by viewModel.postScreenState.collectAsState()
        val postUiState by viewModel.postUiDataState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val contentDescription = remember { mutableStateOf("") }
        val options = PostType.entries.toList()
        var selected by remember { mutableStateOf(0) }

        // Handle navigation effects
        LaunchedEffect(postUiState) {
            when (postUiState) {
                is CreatePostUiState.Success -> {
                    navigator.popUntilRoot()
                }
                is CreatePostUiState.NavigateToHome -> {
                    navigator.popUntilRoot()
                }
                else -> {}
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    title = {
                        TextViewMedium("Create Post")
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
            }) { paddingValues ->
            if (postUiState == CreatePostUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        TextViewBody("Creating your post...")
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(all = 16.dp)
                ) {
                    TextViewBody("Select book")
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
//                                    navigator.push(SearchBooksScreen())
                                },
                                indication = ripple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    ) {
                        OutlinedTextField(
                            value = postScreenState.selectedBook?.volumeInfo?.title ?: "",
                            onValueChange = {},
                            placeholder = {
                                TextViewBody(
                                    "Search book",
                                    color = Color.LightGray
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "")
                            },
                            shape = RoundedCornerShape(12.dp),
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    PostTypeSelector(
                        options = options,
                        selectedIndex = selected,
                        onSelected = {
                            selected = it
                        }
                    )
                    Spacer(Modifier.height(24.dp))
                    TextViewBody("Your thoughts")
                    Spacer(Modifier.fillMaxWidth().height(16.dp))
                    OutlinedTextField(
                        value = contentDescription.value,
                        onValueChange = { contentDescription.value = it },
                        placeholder = {
                            TextViewBody(
                                "What do you think about this book ?",
                                color = Color.LightGray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = Color.Blue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                navigator.pop()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            TextViewBody("Cancel")
                        }
                        Button(
                            enabled = postScreenState.selectedBook != null &&
                                    contentDescription.value.isNotEmpty() &&
                                    postUiState != CreatePostUiState.Loading,
                            onClick = {
                                viewModel.updateUiState(
                                    postScreenState.copy(
                                        postCaption = contentDescription.value,
                                        bookTitle = postScreenState.selectedBook?.volumeInfo?.title ?: "",
                                        selectedPostType = options[selected]
                                    )
                                )
                                viewModel.createPost()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Blue,
                                contentColor = Color.White,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.Gray
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            TextViewBody("Post", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostTypeSelector(
    options: List<PostType>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Row {
        options.forEachIndexed { i, option ->
            val isSelected = i == selectedIndex
            Button(
                onClick = { onSelected(i) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color.Blue else Color.LightGray,
                    contentColor = if (isSelected) Color.White else Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                modifier = Modifier.padding(end = 4.dp)
            ) {
                TextViewBody(
                    option.name.toCamelCase(),
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}