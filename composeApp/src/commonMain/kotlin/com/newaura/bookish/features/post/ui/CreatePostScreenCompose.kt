package com.newaura.bookish.features.post.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.core.screen.Screen
import com.newaura.bookish.features.post.ButtonState
import com.newaura.bookish.features.post.CreatePostViewModel
import com.newaura.bookish.features.post.ImageFile
import com.newaura.bookish.model.PostType
import coil3.compose.AsyncImage

/**
 * Compose implementation example for CreatePostScreen
 * Shows how to integrate with CreatePostViewModel
 */
class CreatePostScreenCompose : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: CreatePostViewModel = remember { CreatePostViewModel(TODO()) } // Inject from DI

        val postCaption by viewModel.postCaption.collectAsState()
        val bookTitle by viewModel.bookTitle.collectAsState()
        val bookLink by viewModel.bookLink.collectAsState()
        val selectedImages by viewModel.selectedImages.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val buttonState by viewModel.buttonState.collectAsState()
        val selectedPostType by viewModel.selectedPostType.collectAsState()
        val postScreenState by viewModel.postScreenState.collectAsState()

        // Handle state navigation
        LaunchedEffect(postScreenState.uiState) {
            when (postScreenState.uiState) {
                is CreatePostUiState.Success -> {
                    // Navigate back to home
                    navigator.pop()
                }
                is CreatePostUiState.Error -> {
                    // Show error toast
                    val error = postScreenState.uiState as CreatePostUiState.Error
                    // showToast(error.message)
                }
                is CreatePostUiState.NavigateToSearch -> {
                    // Navigate to search screen
                    // navigator.push(SearchSuggestionScreen())
                }
                is CreatePostUiState.NavigateToHome -> {
                    navigator.pop()
                }
                else -> {}
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Create Post") },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.clickable {
                                navigator.pop()
                            }
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Select Book Section
                Text("Select Book", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = bookTitle,
                    onValueChange = { viewModel.updateBookTitle(it) },
                    placeholder = { Text("Search book") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Search",
                            modifier = Modifier.clickable {
                                viewModel.navigateToSearchSuggestion { bookDetail ->
                                    if (bookDetail != null) {
                                        viewModel.setSelectedBook(bookDetail)
                                    }
                                }
                            }
                        )
                    }
                )

                // Post Type Selector
                Text("Post Type", style = MaterialTheme.typography.titleMedium)
                PostTypeSelector(
                    options = PostType.entries,
                    selectedIndex = PostType.entries.indexOf(selectedPostType),
                    onSelected = { index ->
                        viewModel.updatePostType(PostType.entries[index])
                    }
                )

                // My Thoughts Section
                Text("My Thoughts", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = postCaption,
                    onValueChange = { viewModel.updatePostCaption(it) },
                    placeholder = { Text("What do you think about this book?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )

                // Book Link Section
                Text("Book Link (Optional)", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = bookLink,
                    onValueChange = { viewModel.updateBookLink(it) },
                    placeholder = { Text("Paste book link") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )

                // Images Section
                if (selectedImages.isNotEmpty()) {
                    Text("Images (${selectedImages.size})", style = MaterialTheme.typography.titleMedium)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(selectedImages) { index, imageFile ->
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                AsyncImage(
                                    model = imageFile.path,
                                    contentDescription = "Selected image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .clickable {
                                            viewModel.removeImage(index)
                                        }
                                        .padding(4.dp)
                                        .size(20.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // Add Images Button
                Button(
                    onClick = {
                        // Show image picker dialog
                        // This should be handled in the Composable, not ViewModel
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Add Images")
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { navigator.pop() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(),
                        enabled = !isLoading
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { viewModel.createPost() },
                        modifier = Modifier.weight(1f),
                        enabled = buttonState == ButtonState.ENABLED && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Post")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper composable for post type selection
 */
@Composable
fun PostTypeSelector(
    options: List<PostType>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, postType ->
            FilterChip(
                selected = selectedIndex == index,
                onClick = { onSelected(index) },
                label = { Text(postType.name.lowercase()) }
            )
        }
    }
}

/**
 * Helper composable for showing images
 */
@Composable
fun ImagePreview(
    imageFile: ImageFile,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = imageFile.path,
            contentDescription = "Preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable { onRemove() }
                .padding(4.dp),
            tint = Color.White
        )
    }
}

