package com.newaura.bookish.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.domain.extensions.loadPainter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImagePickerLauncher

@Composable
fun CustomPermissionDialog(
    innerPadding: PaddingValues,
    showImagePickerLauncher: Boolean,
    onShowImagePickerLauncherChange: (Boolean) -> Unit,
    onPickerSheetIOSVisibleChange: (Boolean) -> Unit,
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
                            cameraCaptureConfig = CameraCaptureConfig(
                                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                                    customDeniedDialog = { onRetry ->
                                        DialogDeniedCameraPermission(
                                            title = "Permission required",
                                            message = "We need access to the camera to take photos",
                                            onRetry = onRetry,
                                            onDismiss = {
                                                onShowImagePickerLauncherChange(false)
                                                onPickerSheetIOSVisibleChange(false)
                                            }
                                        )
                                    },
                                    customSettingsDialog = { onOpenSettings ->
                                        CustomGoToSettingsDialog(
                                            title = "Go to settings",
                                            message = "Go to Settings > Permissions > Camera",
                                            onOpenSettings = onOpenSettings,
                                            onDismiss = {
                                                onShowImagePickerLauncherChange(false)
                                                onPickerSheetIOSVisibleChange(false)
                                            }
                                        )
                                    }
                                )
                            )
                        )
                    )
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

@Composable
fun CustomGoToSettingsDialog(title: String, message: String, onOpenSettings: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = message,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = onOpenSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Settings")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun DialogDeniedCameraPermission(
    title: String,
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = message,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Allow Permission")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}