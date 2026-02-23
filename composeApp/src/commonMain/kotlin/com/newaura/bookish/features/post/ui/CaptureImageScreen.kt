package com.newaura.bookish.features.post.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.core.common.TextViewBody
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImagePickerLauncher

enum class PickImage {
    FROM_GALLERY,
    FROM_CAMERA
}

class CaptureImageScreen(
    private val pickImage: PickImage,
    private val onCameraImageResult: (List<PhotoResult>) -> Unit = {},
    private val onGalleryImageResult: (List<GalleryPhotoResult>) -> Unit = {},
    private val onDismiss: () -> Unit
) : Screen {

    @Composable
    override fun Content() {
        var errorMessage by remember { mutableStateOf("") }
        val cameraImageList = remember { mutableStateListOf<PhotoResult>() }
        val galleryImageList = remember { mutableStateListOf<GalleryPhotoResult>() }
        val navigator = LocalNavigator.currentOrThrow

        Scaffold {
            innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    when (pickImage) {
                        PickImage.FROM_GALLERY -> {
                            println("📸 Rendering GalleryPickerLauncher")
                            GalleryPickerLauncher(
                                onPhotosSelected = { result ->
                                    println("🎯 GalleryPickerLauncher.onPhotosSelected called with ${result.size} photos")
                                    result.forEach { photo ->
                                        println("   - Photo: ${photo.fileName}, URI: ${photo.uri}")
                                    }
                                    galleryImageList.clear()
                                    galleryImageList.addAll(result)
                                    println("🎯 Calling onGalleryImageResult callback")
                                    onGalleryImageResult(galleryImageList.toList())
                                    println("🎯 Popping navigator")
                                    navigator.pop()
                                },
                                onError = { error ->
                                    println("❌ GalleryPickerLauncher.onError: ${error.message}")
                                    errorMessage = error.message ?: "Something went wrong"
                                },
                                onDismiss = {
                                    println("⚠️  GalleryPickerLauncher.onDismiss called")
                                    onDismiss()
                                    navigator.pop()
                                }
                            )
                        }

                        PickImage.FROM_CAMERA -> {
                            println("📷 Rendering ImagePickerLauncher")
                            ImagePickerLauncher(
                                config = ImagePickerConfig(
                                    onPhotoCaptured = { result ->
                                        println("🎯 ImagePickerLauncher.onPhotoCaptured called")
                                        println("   - Photo: ${result.fileName}, URI: ${result.uri}")
                                        cameraImageList.clear()
                                        cameraImageList.add(result)
                                        println("🎯 Calling onCameraImageResult callback")
                                        onCameraImageResult(cameraImageList.toList())
                                        println("🎯 Popping navigator")
                                    },
                                    onError = { error ->
                                        println("❌ ImagePickerLauncher.onError: ${error.message}")
                                        errorMessage = error.message ?: "Something went wrong"
                                    },
                                    onDismiss = {
                                        println("⚠️  ImagePickerLauncher.onDismiss called")
                                        onDismiss()
                                        navigator.pop()
                                    },
                                    cameraCaptureConfig = CameraCaptureConfig(
                                        includeExif = true,
                                        permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                                            cancelButtonTextIOS = "Dismiss",
                                            onCancelPermissionConfigIOS = {
                                                println("⚠️  Permission cancelled on iOS")
                                                onDismiss()
                                                navigator.pop()
                                            }
                                        )
                                    ),
                                    directCameraLaunch = true
                                )
                            )
                        }
                    }
                }
                if (errorMessage.isNotEmpty()) {
                    ShowErrorWidget(errorMessage)
                }
            }
        }
    }

    @Composable
    private fun ShowErrorWidget(message: String) {
        TextViewBody(message)
    }
}