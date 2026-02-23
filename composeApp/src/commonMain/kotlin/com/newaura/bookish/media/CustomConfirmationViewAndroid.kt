package com.newaura.bookish.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.domain.extensions.loadPainter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImagePickerLauncher

@Composable
fun CustomConfirmationViewAndroid(
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
                                    customConfirmationView = { photoResult, onConfirm, onRetry ->
                                        CustomAndroidConfirmationView(
                                            result = photoResult,
                                            onConfirm = onConfirm,
                                            onRetry = onRetry
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
fun CustomAndroidConfirmationView(
    result: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Review photo",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AsyncImage(
                model = result.uri,
                contentDescription = "Captured photo preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { onRetry() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(text = "Retry")
            }

            Button(
                onClick = { onConfirm(result) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Confirm", color = Color.White)
            }
        }
    }
}