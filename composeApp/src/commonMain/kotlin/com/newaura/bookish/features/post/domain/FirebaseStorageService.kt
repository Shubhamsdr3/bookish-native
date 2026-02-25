package com.newaura.bookish.features.post.domain

import com.newaura.bookish.features.post.data.dto.ImageFile
import kotlinx.coroutines.flow.Flow

/**
 * Interface for Firebase Storage operations
 * Implemented per platform (Android, iOS, Web)
 */
interface FirebaseStorageService {
    /**
     * Upload an image file to Firebase Storage
     * @param imageFile The image file to upload
     * @return Flow emitting the download URL when upload completes
     * @throws Exception if upload fails
     */
    suspend fun uploadImage(imageFile: ImageFile): Flow<Result<String>>

    /**
     * Upload multiple images to Firebase Storage
     * @param imageFiles List of image files to upload
     * @return Flow emitting the list of download URLs
     */
    suspend fun uploadImages(imageFiles: List<ImageFile>): Flow<Result<List<String>>>

    /**
     * Delete an image from Firebase Storage
     * @param imageUrl The download URL of the image to delete
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit>
}

