package com.newaura.bookish.features.post.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import java.io.File

class ImageCompressionUtil(private val context: Context) {

    /**
     * Compress an image file.
     * Handles both file:// URIs and content:// URIs
     */
    suspend fun compressImage(imageFile: File): File {
        // Convert to local file if it's a content URI
        val localFile = copyContentUriToCache(imageFile)

        return Compressor.compress(context, localFile) {
            resolution(1280, 720)
            quality(80)
            format(Bitmap.CompressFormat.JPEG)
            destination(File(context.cacheDir, "compressed_${System.currentTimeMillis()}_${localFile.name}"))
        }
    }

    suspend fun compressImages(imageFiles: List<File>): List<File> {
        return imageFiles.map { compressImage(it) }
    }

    /**
     * Copy content:// URIs to a local cache file
     * Handles cloud provider URIs that don't have local file paths
     */
    private fun copyContentUriToCache(file: File): File {
        val filePath = file.absolutePath

        // If it's already a local file, return as-is
        if (filePath.startsWith("/") && !filePath.contains("content://")) {
            return file
        }

        // If it's a content:// URI, copy to cache
        return try {
            val uri = when {
                filePath.startsWith("content://") -> filePath.toUri()
                else -> return file
            }

            val cacheFile = File(context.cacheDir, "${System.currentTimeMillis()}_${file.name}")
            context.contentResolver.openInputStream(uri)?.use { input ->
                cacheFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if (cacheFile.exists()) {
                cacheFile
            } else {
                file
            }
        } catch (_: Exception) {
            // If anything fails, return original file
            file
        }
    }
}