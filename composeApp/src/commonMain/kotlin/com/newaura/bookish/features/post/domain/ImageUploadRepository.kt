package com.newaura.bookish.features.post.domain

import kotlinx.coroutines.flow.Flow

/**
 * Repository for image upload operations
 * Provides a clean abstraction over Firebase Storage and Upload Manager
 */
interface ImageUploadRepository {
    /**
     * Upload a single image to Firebase Storage
     * @param imageUri The image to upload
     * @return Flow emitting the download URL
     */
    suspend fun uploadImage(imageUri: String): Flow<Result<String>>

    /**
     * Upload multiple images to Firebase Storage
     * @param imageUris List of images to upload
     * @return Flow emitting list of download URLs
     */
    suspend fun uploadImages(imageUris: List<String>): Flow<Result<List<String>>>

    /**
     * Delete an image from Firebase Storage
     * @param imageUrl The download URL
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit>

    /**
     * Schedule background image upload with WorkManager
     * @param imageUris List of image file paths to upload
     * @param workTag Tag to identify the work
     * @return Work ID for tracking
     */
    fun scheduleBackgroundUpload(imageUris: List<String>, workTag: String = "image_upload"): String

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