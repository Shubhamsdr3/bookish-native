package com.newaura.bookish.features.post.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import java.io.File
import java.io.FileInputStream

actual data class PlatformImageFile(
    val name: String,
    val path: String,
    val byteArray: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlatformImageFile) return false
        return name == other.name && path == other.path
    }

    override fun hashCode(): Int {
        return name.hashCode() + path.hashCode()
    }
}

/**
 * Android implementation using native Android image picker
 * Note: This is a helper function - for actual integration in Compose,
 * use rememberLauncherForActivityResult in your Composable
 */
actual suspend fun pickImagesFromGallery(quality: Int): List<PlatformImageFile> {
    // Implementation note: This should be called from a Composable using:
    // val pickImages = rememberLauncherForActivityResult(
    //     ActivityResultContracts.GetMultipleContents()
    // ) { uris ->
    //     uris.forEach { uri ->
    //         val inputStream = contentResolver.openInputStream(uri)
    //         // Convert to ImageFile
    //     }
    // }

    return emptyList() // Placeholder
}

actual suspend fun captureImageFromCamera(): PlatformImageFile? {
    // Implementation note: Use ActivityResultContracts.TakePicture()
    // val takePicture = rememberLauncherForActivityResult(
    //     ActivityResultContracts.TakePicture()
    // ) { success ->
    //     if (success) {
    //         // File is already saved to the URI provided
    //     }
    // }

    return null // Placeholder
}

actual suspend fun pickVideoFromGallery(maxDurationSeconds: Int): PlatformImageFile? {
    // Implementation note: Use ActivityResultContracts.GetContent() with video MIME type
    // val pickVideo = rememberLauncherForActivityResult(
    //     ActivityResultContracts.GetContent()
    // ) { uri: Uri? ->
    //     uri?.let {
    //         // Read video file
    //     }
    // }

    return null // Placeholder
}

/**
 * Helper function to convert Uri to ByteArray on Android
 */
fun Uri.readBytes(context: Context): ByteArray {
    return context.contentResolver.openInputStream(this)?.use { inputStream ->
        inputStream.readBytes()
    } ?: byteArrayOf()
}

/**
 * Helper function to get filename from Uri
 */
fun Uri.getFileName(context: Context): String {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    return cursor?.use {
        val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        it.moveToFirst()
        it.getString(nameIndex)
    } ?: "image_${System.currentTimeMillis()}.jpg"
}

