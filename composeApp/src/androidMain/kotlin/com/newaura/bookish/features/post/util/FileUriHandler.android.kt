package com.newaura.bookish.features.post.util

import android.net.Uri
import coil3.request.ImageRequest
import java.io.File

actual fun convertFileUriToCoilModel(filePath: String): Any {
    return when {
        filePath.startsWith("file://") -> {
            // Remove file:// prefix and create a File object
            val actualPath = filePath.removePrefix("file://")
            File(actualPath)
        }
        filePath.startsWith("content://") -> {
            // Keep content URIs as is - Coil handles them
            filePath
        }
        else -> {
            // Assume it's a direct file path
            File(filePath)
        }
    }
}

actual fun isLocalFileUri(uri: String): Boolean {
    return uri.startsWith("file://") || uri.startsWith("/storage/")
}

