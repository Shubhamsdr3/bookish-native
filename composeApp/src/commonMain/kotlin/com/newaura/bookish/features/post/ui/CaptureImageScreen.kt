package com.newaura.bookish.features.post.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.util.AppLogger
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
    private val onCameraImageResult: (PhotoResult) -> Unit = {},
    private val onGalleryImageResult: (List<GalleryPhotoResult>) -> Unit = {},
) : Screen {

    @Composable
    override fun Content() {
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
                            GalleryPickerLauncher(
                                allowMultiple = true,
                                onPhotosSelected = { result ->
                                    onGalleryImageResult(result)
                                    navigator.pop()
                                },
                                onError = { error ->
                                    AppLogger.d("GalleryPickerLauncher.onError: ${error.message}")
                                },
                                onDismiss = {
                                    navigator.pop()
                                }
                            )
                        }

                        PickImage.FROM_CAMERA -> {
                            AppLogger.d("📷 Rendering ImagePickerLauncher")
                            ImagePickerLauncher(
                                config = ImagePickerConfig(
                                    onPhotoCaptured = { result ->
                                        onCameraImageResult(result)
                                        navigator.pop()
                                    },
                                    onError = { error ->
                                        AppLogger.d("ImagePickerLauncher.onError: ${error.message}")
                                    },
                                    onDismiss = {
                                        navigator.pop()
                                    },
                                    cameraCaptureConfig = CameraCaptureConfig(
                                        includeExif = true,
                                        permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                                            cancelButtonTextIOS = "Dismiss",
                                            onCancelPermissionConfigIOS = {
                                                navigator.pop()
                                            },
                                        )
                                    ),
                                    directCameraLaunch = true
                                )
                            )
                        }
                    }
                }
//                if (errorMessage.isNotEmpty()) {
//                    ShowErrorWidget(errorMessage)
//                }
            }
        }
    }

    @Composable
    private fun ShowErrorWidget(message: String) {
        TextViewBody(message)
    }
}