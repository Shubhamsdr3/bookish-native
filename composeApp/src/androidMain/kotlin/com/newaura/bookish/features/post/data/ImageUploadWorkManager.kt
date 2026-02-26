package com.newaura.bookish.features.post.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.domain.ImageUploadManager
import com.newaura.bookish.features.post.domain.UploadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import java.util.concurrent.TimeUnit

class ImageUploadWorkManager(private val context: Context) : ImageUploadManager {

    override fun scheduleImageUpload(imagePaths: List<String>, workTag: String): String {
        try {
            AppLogger.d("📋 Scheduling image upload work: ${imagePaths.size} images")

            val workId = "${ImageUploadWorker.WORK_NAME}_${System.currentTimeMillis()}"

            val uploadWorkRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
                .setInputData(
                    workDataOf(
                        ImageUploadWorker.IMAGE_PATHS_KEY to imagePaths.toTypedArray()
                    )
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    15,
                    TimeUnit.SECONDS
                )
                .addTag(workTag)
                .addTag(ImageUploadWorker.WORK_NAME)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                workId,
                ExistingWorkPolicy.KEEP,
                uploadWorkRequest
            )

            AppLogger.d("✅ Image upload work scheduled successfully with ID: $workId")
            return workId
        } catch (exception: Exception) {
            AppLogger.e(exception)
            exception.printStackTrace()
            throw exception
        }
    }

    /**
     * Observe upload results using Flow for Compose
     */
    override fun observeUploadState(workTag: String): Flow<UploadState> = flow {
        try {
            val workInfosLiveData = WorkManager.getInstance(context)
                .getWorkInfosByTagLiveData(workTag)

            var lastEmittedState: UploadState = UploadState.Idle

            val observer: (List<WorkInfo>) -> Unit = { workInfoList ->
                workInfoList.forEach { workInfo ->
                    val newState = when (workInfo.state) {
                        WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> {
                            AppLogger.d("🔄 Upload in progress...")
                            UploadState.Loading()
                        }

                        WorkInfo.State.SUCCEEDED -> {
                            val uploadedUrls = workInfo.outputData
                                .getStringArray(ImageUploadWorker.UPLOADED_URLS_KEY)
                                ?.toList() ?: emptyList()
                            val imageCount = workInfo.outputData
                                .getInt("image_count", 0)
                            AppLogger.d("✅ Upload complete: $imageCount images uploaded")
                            UploadState.Success(uploadedUrls)
                        }

                        WorkInfo.State.FAILED -> {
                            AppLogger.e("❌ Upload failed")
                            UploadState.Error(Exception("Image upload failed"))
                        }

                        WorkInfo.State.CANCELLED -> {
                            AppLogger.d("⏹️  Upload cancelled")
                            UploadState.Idle
                        }

                        WorkInfo.State.BLOCKED -> {
                            AppLogger.d("⏳ Upload blocked")
                            UploadState.Loading()
                        }
                    }

                    if (newState != lastEmittedState) {
                        lastEmittedState = newState
                    }
                }
            }

            // Collect work info updates
            workInfosLiveData.observeForever(observer)

        } catch (exception: Exception) {
            AppLogger.e(exception)
            emit(UploadState.Error(exception))
        }
    }


    override fun cancelImageUpload(workTag: String) {
        try {
            WorkManager.getInstance(context).cancelAllWorkByTag(workTag)
            AppLogger.d("⏹️  Image upload work cancelled")
        } catch (exception: Exception) {
            AppLogger.e(exception)
        }
    }

    override fun cancelImageUploadById(workId: String) {
        try {
            WorkManager.getInstance(context).cancelWorkById(UUID.fromString(workId))
            AppLogger.d("⏹️  Image upload work cancelled for ID: $workId")
        } catch (exception: Exception) {
            AppLogger.e(exception)
        }
    }
}