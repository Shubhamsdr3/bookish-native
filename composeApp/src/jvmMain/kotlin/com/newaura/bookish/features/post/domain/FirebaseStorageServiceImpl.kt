package com.newaura.bookish.features.post.domain

import com.newaura.bookish.features.post.data.dto.ImageFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * JVM implementation stub for Firebase Storage Service
 * TODO: Implement actual Firebase Storage for JVM
 */
class FirebaseStorageServiceImpl : FirebaseStorageService {

    override suspend fun uploadImage(imageFile: ImageFile): Flow<Result<String>> {
        println("⚠️  Firebase Storage not implemented for JVM")
        return flowOf(Result.failure(Exception("Firebase Storage not implemented for JVM")))
    }

    override suspend fun uploadImages(imageFiles: List<ImageFile>): Flow<Result<List<String>>> {
        println("⚠️  Firebase Storage not implemented for JVM")
        return flowOf(Result.failure(Exception("Firebase Storage not implemented for JVM")))
    }

    override suspend fun deleteImage(imageUrl: String): Result<Unit> {
        println("⚠️  Firebase Storage not implemented for JVM")
        return Result.failure(Exception("Firebase Storage not implemented for JVM"))
    }
}

