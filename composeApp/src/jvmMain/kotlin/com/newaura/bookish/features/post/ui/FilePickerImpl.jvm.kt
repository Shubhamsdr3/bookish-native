package com.newaura.bookish.features.post.ui

import com.newaura.bookish.features.post.data.dto.ImageFile
import com.newaura.bookish.features.post.domain.FilePicker

actual class FilePickerImpl : FilePicker {
    actual override suspend fun pickImagesFromGallery(): List<ImageFile> {
        TODO("Not yet implemented")
    }

    actual override suspend fun pickImageFromCamera(): ImageFile? {
        TODO("Not yet implemented")
    }
}