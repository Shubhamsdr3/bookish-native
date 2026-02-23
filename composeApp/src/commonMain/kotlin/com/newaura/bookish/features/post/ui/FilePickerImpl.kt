package com.newaura.bookish.features.post.ui

import com.newaura.bookish.features.post.data.dto.ImageFile
import com.newaura.bookish.features.post.domain.FilePicker

expect class FilePickerImpl() : FilePicker {
    override suspend fun pickImagesFromGallery(): List<ImageFile>
    override suspend fun pickImageFromCamera(): ImageFile?
}