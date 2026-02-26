package com.newaura.bookish.features.post.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.data.dto.ImageFile
import com.newaura.bookish.features.post.domain.FirebaseStorageService
import com.newaura.bookish.features.post.util.ImageCompressionUtil
import kotlinx.coroutines.flow.first
import java.io.File

class ImageUploadWorker(
    context: Context,
    params: WorkerParameters,
    private val firebaseStorageService: FirebaseStorageService,
    private val compressionUtil: ImageCompressionUtil
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val imagePaths = inputData.getStringArray("image_paths") ?: emptyArray()
            if (imagePaths.isEmpty()) {
                AppLogger.e("No image paths provided")
                return Result.failure()
            }

            val imageFiles = imagePaths.map { File(it) }

            // Compress images
            val compressedFiles = compressionUtil.compressImages(imageFiles)

            // Convert to ImageFile objects
            val imageFileObjects = compressedFiles.map { file ->
                ImageFile(
                    name = file.name,
                    path = file.absolutePath,
                    mimeType = "image/*",
                    fileSize = file.length(),
                    exifData = null
                )
            }

            val uploadResult = firebaseStorageService.uploadImages(imageFileObjects).first()

            uploadResult.onSuccess { urls ->
                AppLogger.d("✅ Upload successful: ${urls.size} images uploaded")
                val outputData = workDataOf(
                    "uploaded_urls" to urls.toTypedArray(),
                    "image_count" to urls.size
                )
                return Result.success(outputData)
            }

            uploadResult.onFailure { exception ->
                AppLogger.e(exception)
                if (runAttemptCount < 3) {
                    return Result.retry()
                }
                return Result.failure()
            }

            Result.failure()

        } catch (exception: Exception) {
            AppLogger.e(exception)
            exception.printStackTrace()

            return if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val WORK_NAME = "image_upload_work"
        const val IMAGE_PATHS_KEY = "image_paths"
        const val UPLOADED_URLS_KEY = "uploaded_urls"
    }
}
