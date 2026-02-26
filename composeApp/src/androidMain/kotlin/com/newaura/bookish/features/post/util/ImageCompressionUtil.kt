package com.newaura.bookish.features.post.util

import android.content.Context
import android.graphics.Bitmap
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import java.io.File

class ImageCompressionUtil(private val context: Context) {

    //TODO delete the file once uploaded.
    suspend fun compressImage(imageFile: File): File {
        return Compressor.compress(context, imageFile) {
            resolution(1280, 720)
            quality(80)
            format(Bitmap.CompressFormat.JPEG)
            destination(File(context.cacheDir, "compressed_${System.currentTimeMillis()}_${imageFile.name}"))
        }
    }

    suspend fun compressImages(imageFiles: List<File>): List<File> {
        return imageFiles.map { compressImage(it) }
    }
}