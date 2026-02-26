package com.newaura.bookish.features.post.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.TextViewMedium
import com.newaura.bookish.core.ui.BookDetailCard
import com.newaura.bookish.core.util.toCamelCase
import com.newaura.bookish.features.post.data.dto.ImageFile
import com.newaura.bookish.features.post.util.convertFileUriToCoilModel
import com.newaura.bookish.model.BookDetail
import com.newaura.bookish.model.PostType
import org.koin.compose.viewmodel.koinViewModel

class CreatePostScreen(val bookDetail: BookDetail) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel: CreatePostViewModel = koinViewModel()
        val postScreenState by viewModel.postScreenState.collectAsState()
        val postUiState by viewModel.postUiDataState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val options = remember { PostType.entries.toList() }
        var selected by remember { mutableStateOf(0) }

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
                        .verticalScroll(rememberScrollState())
                ) {
                    TextViewBody("Selected book")
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BookDetailCard(bookDetail, onClick = {
                            navigator.pop()
                        })
                    }
                    Spacer(Modifier.height(16.dp))
                    PostTypeSelector(
                        options = options,
                        selectedIndex = selected,
                        onSelected = { selected = it }
                    )
                    Spacer(Modifier.height(24.dp))
                    TextViewBody("Your thoughts")
                    Spacer(Modifier.fillMaxWidth().height(16.dp))
                    OutlinedTextField(
                        value = postScreenState.postCaption,
                        onValueChange = { newCaption ->
                            viewModel.updatePostCaption(newCaption)
                        },
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
                            .height(150.dp)
                    )
                    Spacer(Modifier.height(24.dp))

                    TextViewBody("Attach images")
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GalleryButton(
                            modifier = Modifier.weight(1f),
                            onButtonClicked = {
                                navigator.push(CaptureImageScreen(
                                    pickImage = PickImage.FROM_GALLERY,
                                    onGalleryImageResult = { photos ->
                                        viewModel.addGalleryImages(photos)
                                    }
                                ))
                            }
                        )
                        CameraButton(
                            modifier = Modifier.weight(1f),
                            onButtonClicked = {
                                navigator.push(
                                    CaptureImageScreen(
                                        PickImage.FROM_CAMERA,
                                        onCameraImageResult = { photo ->
                                            viewModel.addCameraImage(photo)
                                        },
                                    )
                                )
                            }
                        )
                    }

                    TextViewBody("Selected images (${postScreenState.selectedImages.size})")
                    Spacer(Modifier.height(12.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = postScreenState.selectedImages,
                                key = { imageFile -> "${imageFile.path}${imageFile.name}" }
                            ) { imageFile ->
                                ImageThumbnail(
                                    imageFile = imageFile,
                                    onRemove = {
                                        viewModel.removeImage(imageFile)
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))

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
                        val isEnabled = postScreenState.postCaption.isNotEmpty() &&
                                postUiState != CreatePostUiState.Loading
                        Button(
                            enabled = isEnabled,
                            onClick = {
                                viewModel.updateUiState(
                                    postScreenState.copy(
                                        bookTitle = postScreenState.selectedBook?.volumeInfo?.title
                                            ?: "",
                                        selectedPostType = options[selected],
                                        selectedBook = bookDetail
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
fun ImageThumbnail(
    imageFile: ImageFile,
    onRemove: () -> Unit
) {
    val modelForCoil = convertFileUriToCoilModel(imageFile.path)

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
    ) {
        AsyncImage(
            model = modelForCoil,
            contentDescription = imageFile.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable(indication = ripple(radius = 14.dp),
                    interactionSource = remember { MutableInteractionSource() }) {
                    onRemove()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove image",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
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
                    containerColor = if (isSelected) Color(0xFF6200EE) else Color.LightGray,
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

@Composable
fun GalleryButton(
    modifier: Modifier = Modifier,
    onButtonClicked: () -> Unit
) {
    Button(
        onClick = onButtonClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EE),
            contentColor = Color.White
        ),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Gallery",
            modifier = Modifier.size(20.dp),
            tint = Color.White
        )
        Spacer(Modifier.size(8.dp))
        TextViewBody("Gallery", color = Color.White)
    }
}

@Composable
fun CameraButton(
    modifier: Modifier = Modifier,
    onButtonClicked: () -> Unit
) {
    Button(
        onClick = onButtonClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF03DAC6),
            contentColor = Color.Black
        ),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Camera,
            contentDescription = "Camera",
            modifier = Modifier.size(20.dp),
            tint = Color.Black
        )
        Spacer(Modifier.size(8.dp))
        TextViewBody("Camera", color = Color.Black)
    }
}
