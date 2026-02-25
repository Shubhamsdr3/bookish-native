package com.newaura.bookish.features.post.util

import android.net.Uri
import coil3.request.ImageRequest
import java.io.File

actual fun convertFileUriToCoilModel(filePath: String): Any {
    return when {
        filePath.startsWith("file://") -> {
            val actualPath = filePath.removePrefix("file://")
            File(actualPath)
        }
        filePath.startsWith("content://") -> {
            filePath
        }
        else -> {
            File(filePath)
        }
    }
}

actual fun isLocalFileUri(uri: String): Boolean {
    return uri.startsWith("file://") || uri.startsWith("/storage/")
}

