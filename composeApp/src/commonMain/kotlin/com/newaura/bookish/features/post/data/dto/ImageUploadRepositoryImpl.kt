package com.newaura.bookish.features.post.data.dto

import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.domain.FirebaseStorageService
import com.newaura.bookish.features.post.domain.ImageUploadManager
import com.newaura.bookish.features.post.domain.ImageUploadRepository
import com.newaura.bookish.features.post.domain.UploadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ImageUploadRepositoryImpl(
    private val uploadManager: FirebaseStorageService,
    private val uploadWorkManager: ImageUploadManager
) : ImageUploadRepository {

    override suspend fun uploadImage(imageUri: String): Flow<Result<String>> {
        return uploadManager.uploadImage(imageUri).map { result ->
            result.onSuccess {
                AppLogger.d("📦 Repository: Single image uploaded successfully: $imageUri")
            }
                .onFailure { exception ->
                    AppLogger.e(exception)
                }
            result
        }
    }

    override suspend fun uploadImages(imageUris: List<String>): Flow<Result<List<String>>> {
        return uploadManager.uploadImages(imageUris)
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

    override fun scheduleBackgroundUpload(imageUris: List<String>, workTag: String): String {
        return uploadWorkManager.scheduleImageUpload(imageUris, workTag)
    }

    override fun observeUploadState(workTag: String): Flow<UploadState> {
        return uploadWorkManager.observeUploadState(workTag)
    }

    override fun cancelUpload(workTag: String) {
        uploadWorkManager.cancelImageUpload(workTag)
    }
}