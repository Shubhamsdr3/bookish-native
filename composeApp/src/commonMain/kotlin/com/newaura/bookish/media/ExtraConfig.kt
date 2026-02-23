package com.newaura.bookish.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.SwitchCamera
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.domain.config.CameraCallbacks
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.domain.config.UiConfig
import io.github.ismoy.imagepickerkmp.domain.extensions.loadPainter
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImagePickerLauncher

@Composable
fun ExtraConfig(
    innerPadding: PaddingValues,
    showImagePickerLauncher: Boolean,
    onShowImagePickerLauncherChange: (Boolean) -> Unit,
    showGalleryPickerLauncher: Boolean,
    onShowGalleryPickerLauncherChange: (Boolean) -> Unit,
    onPickerSheetIOSVisibleChange: (Boolean) -> Unit,
    selectedGalleryImages: List<GalleryPhotoResult>,
    onSelectedGalleryImagesChange: (List<GalleryPhotoResult>) -> Unit,
    resultImagePickerLauncher: PhotoResult?,
    onResultImagePickerLauncherChange: (PhotoResult?) -> Unit
){
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
            when {
                // launch ImagePickerLauncher
                showImagePickerLauncher -> {
                    ImagePickerLauncher(
                        config = ImagePickerConfig(
                            onPhotoCaptured = { result ->
                                onResultImagePickerLauncherChange(result)
                                onShowImagePickerLauncherChange(false)
                                onPickerSheetIOSVisibleChange(false)
                            },
                            onError = {
                                onShowImagePickerLauncherChange(false)
                                onPickerSheetIOSVisibleChange(false)
                            },
                            onDismiss = {
                                onShowImagePickerLauncherChange(false)
                                onPickerSheetIOSVisibleChange(false)
                            },
                            directCameraLaunch = true, // launch camera directly in IOS
                            enableCrop = false,
                            cancelText = "change button text IOS default ",
                            dialogTitle = "change dialog title IOS default",
                            takePhotoText = "change button text IOS default",
                            selectFromGalleryText = "change default text",
                            cameraCaptureConfig = CameraCaptureConfig(
                               // includeExif = true, // enabled extraction EXIF data
                                preference = CapturePhotoPreference.FAST, // faster no lag but can't use the flash
                                // preference = CapturePhotoPreference.BALANCED lag balanced but can use the flash
                                // preference = CapturePhotoPreference.QUALITY high quality capture slow can use the flash
                                captureButtonSize = 40.dp, // change button Size
                                compressionLevel = CompressionLevel.HIGH,// compress image in 90% but maintained good quality
                                //compressionLevel = CompressionLevel.LOW ,// compress image in 5% best quality
                               // compressionLevel = CompressionLevel.MEDIUM ,// compress image 75% but maintained good quality

                                uiConfig = UiConfig( // change color size and icon in the camera interface Android
                                    buttonColor = Color.Blue,
                                    iconColor = Color.White,
                                    buttonSize = 24.dp,
                                    flashIcon = Icons.Default.FlashOn,// Put your image vector icon,
                                    switchCameraIcon = Icons.Default.SwitchCamera, // Put your image vector icon
                                    galleryIcon = Icons.Default.BrowseGallery,// Put your image vector icon
                                ),
                                cameraCallbacks = CameraCallbacks(
                                    onCameraReady = { println("Camera ready")},
                                    onCameraSwitch = { println("Camera switched")}
                                ),
                                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                                    skipConfirmation = true, // disable confirmationView after take the photo in android
                                    cancelButtonTextIOS = "change button cancel text affect only ios",
                                    // required if you no active that the cancel button in dialog
                                    // denied in IOS didn't show Apple can reject your apply submission
                                    // if you use customDialogPermission this option is not necessary
                                    onCancelPermissionConfigIOS = {
                                        onShowImagePickerLauncherChange(false)
                                    }
                                )

                                )
                        )
                    )
                }
                // launch GalleryPickerLauncher
                showGalleryPickerLauncher -> {
                    GalleryPickerLauncher(
                        onPhotosSelected = { result ->
                            onSelectedGalleryImagesChange(result)
                            onShowGalleryPickerLauncherChange(false)
                            onPickerSheetIOSVisibleChange(false)
                        },
                        onError = {
                            onShowGalleryPickerLauncherChange(false)
                            onPickerSheetIOSVisibleChange(false)
                        },
                        onDismiss = {
                            onShowGalleryPickerLauncherChange(false)
                            onPickerSheetIOSVisibleChange(false)
                        },
                        allowMultiple = true,
                        mimeTypes = listOf(MimeType.IMAGE_ALL),
                        selectionLimit = 30, // maximum photos can selected the same time please no more 30 because your app can cash
                        enableCrop = false
                       //includeExif = true, // enabled extraction EXIF data
                    )
                }
                // show list image selected to the Gallery
                selectedGalleryImages.isNotEmpty() -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(selectedGalleryImages) { photo ->
                            Card(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                photo.loadPainter()?.let {
                                    Image(
                                        painter = it,
                                        contentDescription = "Selected image",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }

                resultImagePickerLauncher != null -> {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        resultImagePickerLauncher.loadPainter()?.let {
                            Image(
                                painter = it,
                                contentDescription = "Captured photo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                else -> {
                    Text("No image selected", color = Color.Gray)
                }

            }

        }
    }
}
