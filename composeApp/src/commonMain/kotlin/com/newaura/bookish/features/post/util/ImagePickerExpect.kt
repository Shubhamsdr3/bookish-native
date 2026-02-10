package com.newaura.bookish.features.post.util

/**
 * Represents an image file picked from gallery or camera
 */
expect data class PlatformImageFile(
    val name: String,
    val path: String,
    val byteArray: ByteArray
)

/**
 * Expected function to pick multiple images from gallery
 * Platform specific implementations:
 * - Android: Uses Android ImagePicker
 * - iOS: Uses UIImagePickerController or PHPickerViewController
 */
expect suspend fun pickImagesFromGallery(quality: Int = 25): List<PlatformImageFile>

/**
 * Expected function to capture image from camera
 * Platform specific implementations:
 * - Android: Uses Camera intent or in-app camera
 * - iOS: Uses AVFoundation or native camera
 */
expect suspend fun captureImageFromCamera(): PlatformImageFile?

/**
 * Expected function to pick video from gallery
 * Platform specific implementations:
 * - Android: Uses Android ImagePicker
 * - iOS: Uses UIImagePickerController
 */
expect suspend fun pickVideoFromGallery(maxDurationSeconds: Int = 60): PlatformImageFile?

/**
 * Convert platform image file to ImageFile
 */
fun PlatformImageFile.toImageFile(): com.newaura.bookish.features.post.ImageFile {
    return com.newaura.bookish.features.post.ImageFile(
        name = this.name,
        path = this.path,
        byteArray = this.byteArray
    )
}

