package com.newaura.bookish.media

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

class CameraScreen : Screen {

    @Composable
    override fun Content() {
        var showImagePickerLauncher by remember { mutableStateOf(false) }
        var showGalleryPickerLauncher by remember { mutableStateOf(false) }
        var isPickerSheetIOSVisible by remember { mutableStateOf(false) }
        var selectedGalleryImages by remember { mutableStateOf<List<GalleryPhotoResult>>(emptyList()) }
        var resultImagePickerLauncher by remember { mutableStateOf<PhotoResult?>(null) }
        var isOCRActive by remember { mutableStateOf(false) }
        var isInOCRScreen by remember { mutableStateOf(false) }


        Scaffold(
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isOCRActive || isInOCRScreen) {
                        OutlinedButton(
                            onClick = {
                                isOCRActive = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text("Test OCR", color = Color.Blue)
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    } else {
                        if (!isPickerSheetIOSVisible) {
                            OutlinedButton(
                                onClick = {
                                    selectedGalleryImages = emptyList()
                                    resultImagePickerLauncher = null
                                    showImagePickerLauncher = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text("Open Camera")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    selectedGalleryImages = emptyList()
                                    resultImagePickerLauncher = null
                                    showGalleryPickerLauncher = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text("Select from Gallery", color = Color.Blue)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    isOCRActive = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text("Test OCR", color = Color.Blue)
                            }

                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        ) { innerPadding ->
//        OCRUsageScreen(innerPadding, isOCRActive, onOCRActiveChange = { isOCRActive = it })
            BasicUsage(
                innerPadding = innerPadding,
                showImagePickerLauncher = showImagePickerLauncher,
                onShowImagePickerLauncherChange = { showImagePickerLauncher = it },
                showGalleryPickerLauncher = showGalleryPickerLauncher,
                onShowGalleryPickerLauncherChange = { showGalleryPickerLauncher = it },
                onPickerSheetIOSVisibleChange = { isPickerSheetIOSVisible = it },
                selectedGalleryImages = selectedGalleryImages,
                onSelectedGalleryImagesChange = { selectedGalleryImages = it },
                resultImagePickerLauncher = resultImagePickerLauncher,
                onResultImagePickerLauncherChange = { resultImagePickerLauncher = it }
            )
//            CustomPermissionDialog(
//                innerPadding = innerPadding,
//                showImagePickerLauncher = showImagePickerLauncher,
//                onShowImagePickerLauncherChange = { showImagePickerLauncher = it },
//                onPickerSheetIOSVisibleChange = { isPickerSheetIOSVisible = it },
//                resultImagePickerLauncher = resultImagePickerLauncher,
//                onResultImagePickerLauncherChange = { resultImagePickerLauncher = it }
//            )

//            CustomConfirmationViewAndroid(
//                innerPadding = innerPadding,
//                showImagePickerLauncher = showImagePickerLauncher,
//                onShowImagePickerLauncherChange = { showImagePickerLauncher = it },
//                onPickerSheetIOSVisibleChange = { isPickerSheetIOSVisible = it },
//                resultImagePickerLauncher = resultImagePickerLauncher,
//                onResultImagePickerLauncherChange = { resultImagePickerLauncher = it }
//
//            )
//            CustomBottomSheetIOS(
//                innerPadding = innerPadding,
//                showImagePickerLauncher = showImagePickerLauncher,
//                onShowImagePickerLauncherChange = { showImagePickerLauncher = it },
//                onPickerSheetIOSVisibleChange = { isPickerSheetIOSVisible = it },
//                resultImagePickerLauncher = resultImagePickerLauncher,
//                onResultImagePickerLauncherChange = { resultImagePickerLauncher = it }
//            )
//
//            ExtraConfig(
//                innerPadding = innerPadding,
//                showImagePickerLauncher = showImagePickerLauncher,
//                onShowImagePickerLauncherChange = { showImagePickerLauncher = it },
//                showGalleryPickerLauncher = showGalleryPickerLauncher,
//                onShowGalleryPickerLauncherChange = { showGalleryPickerLauncher = it },
//                onPickerSheetIOSVisibleChange = { isPickerSheetIOSVisible = it },
//                selectedGalleryImages = selectedGalleryImages,
//                onSelectedGalleryImagesChange = { selectedGalleryImages = it },
//                resultImagePickerLauncher = resultImagePickerLauncher,
//                onResultImagePickerLauncherChange = { resultImagePickerLauncher = it }
//            )

        }
    }


}