package com.newaura.bookish.features.post.domain.model

import com.newaura.bookish.features.post.data.dto.ImageFile
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult


object ImageFileMapper {
    fun mapGalleryPhotoToImageFile(galleryPhoto: GalleryPhotoResult): ImageFile {
        return ImageFile(
            path = galleryPhoto.uri,
            name = galleryPhoto.fileName ?: "",
            mimeType = galleryPhoto.mimeType ?: "image/*",
            fileSize = galleryPhoto.fileSize ?: 0L,
            exifData = galleryPhoto.exif
        )
    }

    fun mapPhotoToImageFile(photo: PhotoResult): ImageFile {
        return ImageFile(
            path = photo.uri,
            name = photo.fileName ?: "camera_image",
            mimeType = photo.mimeType ?: "image/*",
            fileSize = photo.fileSize ?: 0L,
            exifData = photo.exif
        )
    }

    fun mapMultipleGalleryPhotos(photos: List<GalleryPhotoResult>): List<ImageFile> {
        return photos.map { mapGalleryPhotoToImageFile(it) }
    }
}