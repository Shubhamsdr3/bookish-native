package com.newaura.bookish.features.post.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.domain.FirebaseStorageService
import com.newaura.bookish.features.post.util.ImageCompressionUtil
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class ImageUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    // Lazy inject dependencies
    private val firebaseStorageService: FirebaseStorageService by inject()
    private val compressionUtil: ImageCompressionUtil by inject()

    companion object {
        const val WORK_NAME = "image_upload_work"
        const val IMAGE_PATHS_KEY = "image_paths"
        const val UPLOADED_URLS_KEY = "uploaded_urls"
    }

    override suspend fun doWork(): Result {
        return try {
            val imagePaths = inputData.getStringArray(IMAGE_PATHS_KEY) ?: emptyArray()
            if (imagePaths.isEmpty()) {
                AppLogger.e("No image paths provided")
                return Result.failure()
            }

            val imageFiles = imagePaths.map { File(it) }

//            val compressedFiles = compressionUtil.compressImages(imageFiles)
//            AppLogger.d("✅ Compression complete: ${compressedFiles.size} images")

            val uploadResult = firebaseStorageService.uploadImages(imagePaths.toList()).first()

            uploadResult.onSuccess { urls ->
                val outputData = workDataOf(
                    "uploaded_urls" to urls.toTypedArray(),
                    "image_count" to urls.size
                )
                return Result.success(outputData)
            }.onFailure { exception ->
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
}
