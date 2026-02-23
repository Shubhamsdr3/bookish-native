package com.newaura.bookish.features.post.domain

import com.newaura.bookish.features.post.data.dto.ImageFile

interface FilePicker {
    suspend fun pickImagesFromGallery(): List<ImageFile>
    suspend fun pickImageFromCamera(): ImageFile?
}