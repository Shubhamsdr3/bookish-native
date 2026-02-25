package com.newaura.bookish.features.post.data

import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.data.dto.ImageFile
import com.newaura.bookish.features.post.domain.FirebaseStorageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for image upload operations
 * Provides a clean abstraction over Firebase Storage
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
}

class ImageUploadRepositoryImpl(
    private val firebaseStorageService: FirebaseStorageService
) : ImageUploadRepository {

    override suspend fun uploadImage(imageFile: ImageFile): Flow<Result<String>> {
        println("📦 Repository: Uploading single image: ${imageFile.name}")
        return firebaseStorageService.uploadImage(imageFile)
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
        AppLogger.d("📦 Repository: Uploading ${imageFiles.size} images")
        return firebaseStorageService.uploadImages(imageFiles)
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
        AppLogger.d("📦 Repository: Deleting image: $imageUrl")
        return firebaseStorageService.deleteImage(imageUrl)
    }
}

