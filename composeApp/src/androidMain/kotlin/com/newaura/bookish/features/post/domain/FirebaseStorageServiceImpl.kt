package com.newaura.bookish.features.post.domain

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.newaura.bookish.features.post.data.dto.ImageFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import androidx.core.net.toUri
import com.newaura.bookish.core.util.AppLogger

/**
 * Android implementation of Firebase Storage Service
 * Uses Firebase Storage SDK for Android
 */

//TODO convert this into workmanager.
class FirebaseStorageServiceImpl(
    private val context: Context,
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : FirebaseStorageService {

    companion object {
        private const val IMAGES_BUCKET = "post_images"
    }

    override suspend fun uploadImage(imageFile: ImageFile): Flow<Result<String>> = flow {
        try {
            val timestamp = System.currentTimeMillis()
            val uniqueId = UUID.randomUUID().toString()
            val fileName = "${IMAGES_BUCKET}/${timestamp}_${uniqueId}_${imageFile.name}"

            // Get file from the path
            val file = when {
                imageFile.path.startsWith("file://") -> {
                    File(imageFile.path.removePrefix("file://"))
                }
                imageFile.path.startsWith("content://") -> {
                    // For content:// URIs, we need to copy to cache
                    val cacheFile = File(context.cacheDir, "${System.currentTimeMillis()}_${imageFile.name}")
                    context.contentResolver.openInputStream(imageFile.path.toUri())?.use { input ->
                        cacheFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    cacheFile
                }
                else -> {
                    File(imageFile.path)
                }
            }

            if (!file.exists()) {
                throw Exception("Image file not found: ${imageFile.path}")
            }

            AppLogger.d("📁 File to upload: ${file.absolutePath}, Size: ${file.length()} bytes")

            // Upload to Firebase Storage
            val storageRef = storage.reference.child(fileName)
            val fileUri = Uri.fromFile(file)

            AppLogger.d("☁️  Uploading to Firebase Storage: $fileName")
            val uploadTask = storageRef.putFile(fileUri).await()

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

    override suspend fun uploadImages(imageFiles: List<ImageFile>): Flow<Result<List<String>>> = flow {
        try {
            AppLogger.d("📤 Starting batch image upload: ${imageFiles.size} images")

            val uploadedUrls = mutableListOf<String>()

            for ((index, imageFile) in imageFiles.withIndex()) {
                AppLogger.d("📸 Uploading image ${index + 1}/${imageFiles.size}: ${imageFile.name}")

                try {
                    // Upload each image sequentially
                    val timestamp = System.currentTimeMillis()
                    val uniqueId = UUID.randomUUID().toString()
                    val fileName = "${IMAGES_BUCKET}/${timestamp}_${uniqueId}_${imageFile.name}"

                    val file = when {
                        imageFile.path.startsWith("file://") -> {
                            File(imageFile.path.removePrefix("file://"))
                        }
                        imageFile.path.startsWith("content://") -> {
                            val cacheFile = File(context.cacheDir, "${System.currentTimeMillis()}_${imageFile.name}")
                            context.contentResolver.openInputStream(imageFile.path.toUri())?.use { input ->
                                cacheFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            cacheFile
                        }
                        else -> {
                            File(imageFile.path)
                        }
                    }

                    if (file.exists()) {
                        val storageRef = storage.reference.child(fileName)
                        val fileUri = Uri.fromFile(file)

                        storageRef.putFile(fileUri).await()
                        val downloadUrl = storageRef.downloadUrl.await()
                        uploadedUrls.add(downloadUrl.toString())

                        AppLogger.d("✅ Image ${index + 1} uploaded: ${downloadUrl}")
                    } else {
                        throw Exception("File not found: ${imageFile.path}")
                    }
                } catch (e: Exception) {
                    AppLogger.e(e)
                    throw e
                }
            }

            AppLogger.d("✅ All images uploaded successfully: ${uploadedUrls.size} URLs obtained")
            emit(Result.success(uploadedUrls))

        } catch (exception: Exception) {
            AppLogger.e(exception)
            exception.printStackTrace()
            emit(Result.failure(exception))
        }
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
}

