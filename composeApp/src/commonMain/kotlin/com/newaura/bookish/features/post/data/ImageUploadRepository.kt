package com.newaura.bookish.features.post.data

import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.data.dto.ImageFile
import com.newaura.bookish.features.post.domain.FirebaseStorageService
import com.newaura.bookish.features.post.domain.ImageUploadManager
import com.newaura.bookish.features.post.domain.UploadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for image upload operations
 * Provides a clean abstraction over Firebase Storage and Upload Manager
 */
interface ImageUploadRepository {
    /**
     * Upload a single image to Firebase Storage
     * @param imageFile The image to upload
     * @return Flow emitting the download URL
     */
    suspend fun uploadImage(imageFile: ImageFile): Flow<Result<String>>

    /**
     * Upload multiple images to Firebase Storage
     * @param imageFiles List of images to upload
     * @return Flow emitting list of download URLs
     */
    suspend fun uploadImages(imageFiles: List<ImageFile>): Flow<Result<List<String>>>

    /**
     * Delete an image from Firebase Storage
     * @param imageUrl The download URL
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit>

    /**
     * Schedule background image upload with WorkManager
     * @param imagePaths List of image file paths to upload
     * @param workTag Tag to identify the work
     * @return Work ID for tracking
     */
    fun scheduleBackgroundUpload(imagePaths: List<String>, workTag: String = "image_upload"): String

    /**
     * Observe upload state changes
     * @param workTag Tag to identify which work to observe
     * @return Flow emitting UploadState changes
     */
    fun observeUploadState(workTag: String = "image_upload"): Flow<UploadState>

    /**
     * Cancel upload work
     * @param workTag Tag to identify which work to cancel
     */
    fun cancelUpload(workTag: String = "image_upload")
}

class ImageUploadRepositoryImpl(
    private val uploadManager: FirebaseStorageService,
    private val uploadWorkManager: ImageUploadManager
) : ImageUploadRepository {

    override suspend fun uploadImage(imageFile: ImageFile): Flow<Result<String>> {
        return uploadManager.uploadImage(imageFile)
            .map { result ->
                result.onSuccess { url ->
                    AppLogger.d("📦 Repository: Single image uploaded successfully: $url")
                }.onFailure { exception ->
                    AppLogger.e(exception)
                }
                result
            }
    }

    override suspend fun uploadImages(imageFiles: List<ImageFile>): Flow<Result<List<String>>> {
        return uploadManager.uploadImages(imageFiles)
            .map { result ->
                result.onSuccess { urls ->
                    AppLogger.d("📦 Repository: All images uploaded successfully: ${urls.size} URLs")
                }.onFailure { exception ->
                    AppLogger.e(exception)
                }
                result
            }
    }

    override suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return uploadManager.deleteImage(imageUrl)
    }

    override fun scheduleBackgroundUpload(imagePaths: List<String>, workTag: String): String {
        AppLogger.d("📋 Repository: Scheduling background upload for ${imagePaths.size} images")
        return uploadWorkManager.scheduleImageUpload(imagePaths, workTag)
    }

    override fun observeUploadState(workTag: String): Flow<UploadState> {
        AppLogger.d("👁️  Repository: Observing upload state for tag: $workTag")
        return uploadWorkManager.observeUploadState(workTag)
    }

    override fun cancelUpload(workTag: String) {
        AppLogger.d("⏹️  Repository: Cancelling upload for tag: $workTag")
        uploadWorkManager.cancelImageUpload(workTag)
    }
}

