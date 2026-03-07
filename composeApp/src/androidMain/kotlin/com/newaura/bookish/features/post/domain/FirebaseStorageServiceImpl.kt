package com.newaura.bookish.features.post.domain

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import androidx.core.net.toUri
import com.newaura.bookish.core.util.AppLogger
import kotlinx.coroutines.flow.catch

/**
 * Android implementation of Firebase Storage Service
 * Uses Firebase Storage SDK for Android
 */
class FirebaseStorageServiceImpl(
    private val context: Context,
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : FirebaseStorageService {

    companion object {
        private const val IMAGES_BUCKET = "post_images"
    }

    override suspend fun uploadImage(imageUri: String): Flow<Result<String>> = flow {
        try {
            val imageFile = getFileFromPath(imageUri)

            val timestamp = System.currentTimeMillis()
            val uniqueId = UUID.randomUUID().toString()
            val fileName = "${IMAGES_BUCKET}/${timestamp}_${uniqueId}_${imageFile.name}"

            if (!imageFile.exists()) {
                throw Exception("Image file not found: ${imageFile.absolutePath}")
            }

            AppLogger.d("📁 File to upload: ${imageFile.absolutePath}, Size: ${imageFile.length()} bytes")

            // Upload to Firebase Storage
            val storageRef = storage.reference.child(fileName)
            val fileUri = Uri.fromFile(imageFile)

            AppLogger.d("☁️  Uploading to Firebase Storage: $fileName")
            storageRef.putFile(fileUri).await()

            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await()
            val urlString = downloadUrl.toString()

            AppLogger.d("✅ Upload successful: $urlString")
            emit(Result.success(urlString))

        } catch (exception: Exception) {
            AppLogger.e(exception)
            exception.printStackTrace()
            emit(Result.failure(exception))
        }
    }

    override suspend fun uploadImages(imageUris: List<String>): Flow<Result<List<String>>> = flow {
        val uploadedUrls = mutableListOf<String>()
        for ((index, imageUri) in imageUris.withIndex()) {
            val imageFile = getFileFromPath(imageUri)

            val timestamp = System.currentTimeMillis()
            val uniqueId = UUID.randomUUID().toString()
            val fileName = "${IMAGES_BUCKET}/${timestamp}_${uniqueId}_${imageFile.name}"

            if (imageFile.exists()) {
                val storageRef = storage.reference.child(fileName)
                val fileUri = Uri.fromFile(imageFile)

                AppLogger.d("📸 Uploading image ${index + 1}/${imageUris.size}: ${imageFile.name}")
                storageRef.putFile(fileUri).await()
                val downloadUrl = storageRef.downloadUrl.await()
                uploadedUrls.add(downloadUrl.toString())

                AppLogger.d("✅ Image ${index + 1} uploaded successfully")
            } else {
                throw Exception("File not found: ${imageFile.absolutePath}")
            }
        }
        AppLogger.d("✅ All ${uploadedUrls.size} images uploaded successfully")
        emit(Result.success(uploadedUrls))

    }.catch { exception ->
        AppLogger.e("❌ Batch upload failed: ${exception.message}")
        exception.printStackTrace()
        emit(Result.failure(exception))
    }

    override suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            AppLogger.d("🗑️  Deleting image: $imageUrl")
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            ref.delete().await()
            AppLogger.d("✅ Image deleted successfully")
            Result.success(Unit)
        } catch (exception: Exception) {
            AppLogger.e(exception)
            exception.printStackTrace()
            Result.failure(exception)
        }
    }

    /**
     * Convert any file path format to an actual File object
     * Handles: file://, content://, and direct paths
     */
    private fun getFileFromPath(path: String): File {
        return when {
            // Handle file:// URIs - remove the prefix
            path.startsWith("file://") -> {
                File(path.removePrefix("file://"))
            }
            // Handle content:// URIs - copy to cache first
            path.startsWith("content://") -> {
                val cacheFile = File(
                    context.cacheDir,
                    "${System.currentTimeMillis()}_${path.substringAfterLast('/')}"
                )
                context.contentResolver.openInputStream(path.toUri())?.use { input ->
                    cacheFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                cacheFile
            }
            // Direct file paths - use as-is
            else -> {
                File(path)
            }
        }
    }
}

