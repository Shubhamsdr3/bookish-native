package com.newaura.bookish.features.post.post

import androidx.compose.runtime.Composable
import com.newaura.bookish.features.post.data.dto.ImageFile

actual suspend fun pickImagesFromGallery(): List<ImageFile> {
    TODO("Not yet implemented")
}

actual suspend fun pickImageFromCamera(): ImageFile? {
    TODO("Not yet implemented")
}

@Composable
actual fun ImagePickerInitializer() {
    // JVM doesn't need context initialization
}
