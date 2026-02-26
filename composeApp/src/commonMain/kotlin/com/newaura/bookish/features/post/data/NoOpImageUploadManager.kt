package com.newaura.bookish.features.post.data

import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.domain.ImageUploadManager
import com.newaura.bookish.features.post.domain.UploadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock

/**
 * No-op implementation of ImageUploadManager for non-Android platforms
 * (iOS, Web, etc. don't have direct WorkManager equivalent)
 */
class NoOpImageUploadManager : ImageUploadManager {

    override fun scheduleImageUpload(imagePaths: List<String>, workTag: String): String {
        AppLogger.d("⚠️  NoOpImageUploadManager: scheduleImageUpload called (no-op on this platform)")
        return "${Clock.System.now()}"
    }

    override fun observeUploadState(workTag: String): Flow<UploadState> = flow {
        AppLogger.d("⚠️  NoOpImageUploadManager: observeUploadState called (returning Idle on this platform)")
        emit(UploadState.Idle)
    }

    override fun cancelImageUpload(workTag: String) {
        AppLogger.d("⚠️  NoOpImageUploadManager: cancelImageUpload called (no-op on this platform)")
    }

    override fun cancelImageUploadById(workId: String) {
        AppLogger.d("⚠️  NoOpImageUploadManager: cancelImageUploadById called (no-op on this platform)")
    }
}

