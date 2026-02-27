package com.newaura.bookish.features.post.domain

import kotlinx.coroutines.flow.Flow

/**
 * Common interface for image upload management
 * Abstracts the platform-specific implementation (Android WorkManager)
 */
sealed class UploadState {
    object Idle : UploadState()
    data class Loading(val progress: Int = 0) : UploadState()
    data class Success(val uploadedUrls: List<String>) : UploadState()
    data class Error(val exception: Exception) : UploadState()
}

interface ImageUploadManager {
    /**
     * Schedule image upload with background processing
     * @param imageUriList List of image uris.
     * @param workTag Tag to identify the work
     * @return Work ID for tracking
     */
    fun scheduleImageUpload(imageUriList: List<String>, workTag: String = "image_upload"): String

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
    fun cancelImageUpload(workTag: String = "image_upload")

    /**
     * Cancel upload work by ID
     * @param workId Work ID to cancel
     */
    fun cancelImageUploadById(workId: String)
}

