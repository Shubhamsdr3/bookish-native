package com.newaura.bookish.features.post.data.dto

import io.github.ismoy.imagepickerkmp.domain.models.ExifData

data class ImageFile(
    val contentUri: String,
    val name: String,
    val mimeType: String,
    val fileSize: Long,
    val exifData: ExifData?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageFile) return false
        return name == other.name && contentUri == other.contentUri
    }

    override fun hashCode(): Int {
        return name.hashCode() + contentUri.hashCode()
    }
}