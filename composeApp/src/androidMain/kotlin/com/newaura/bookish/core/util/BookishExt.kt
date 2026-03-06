package com.newaura.bookish.core.util

import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import kotlin.io.copyTo
import kotlin.use

fun Uri.toTempFile(context: android.content.Context): File {

    val fileName = context.contentResolver
        .query(this, null, null, null, null)
        ?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        } ?: "temp_image_${System.currentTimeMillis()}.jpg"

    val tempFile = File(context.cacheDir, fileName)

    context.contentResolver.openInputStream(this)?.use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    } ?: throw Exception("Cannot open InputStream for URI: $this")
    return tempFile
}