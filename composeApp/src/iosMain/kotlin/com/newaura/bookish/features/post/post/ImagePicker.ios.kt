package com.newaura.bookish.features.post.post

import androidx.compose.runtime.Composable
import com.newaura.bookish.features.post.data.dto.ImageFile

actual suspend fun pickImagesFromGallery(): List<ImageFile> {
    // TODO: Implement image picker for iOS using PHPickerViewController
    return emptyList()
}

actual suspend fun pickImageFromCamera(): ImageFile? {
    // TODO: Implement camera picker for iOS using UIImagePickerController
    return null
}

@Composable
actual fun ImagePickerInitializer() {
    // iOS doesn't need context initialization
    // This is a no-op on iOS
}

