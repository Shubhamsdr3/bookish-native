package com.newaura.bookish.features.post.util

actual fun convertFileUriToCoilModel(filePath: String): Any {
    return when {
        filePath.startsWith("file://") -> {
            // iOS can handle file:// URIs directly
            filePath
        }
        else -> {
            // Return as-is for iOS
            filePath
        }
    }
}

actual fun isLocalFileUri(uri: String): Boolean {
    return uri.startsWith("file://") || uri.startsWith("/var/") || uri.startsWith("/tmp/")
}

