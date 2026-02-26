/**
 * All Required Imports for ImageUploadWorkManager Integration
 * Copy-paste these into your files
 */

// ============================================================================
// COMMON: domain/ImageUploadManager.kt
// ============================================================================
import kotlinx.coroutines.flow.Flow

// ============================================================================
// COMMON: data/ImageUploadRepository.kt
// ============================================================================
import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.data.dto.ImageFile
import com.newaura.bookish.features.post.domain.FirebaseStorageService
import com.newaura.bookish.features.post.domain.ImageUploadManager
import com.newaura.bookish.features.post.domain.UploadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ============================================================================
// COMMON: data/NoOpImageUploadManager.kt
// ============================================================================
import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.domain.ImageUploadManager
import com.newaura.bookish.features.post.domain.UploadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// ============================================================================
// COMMON: ui/CreatePostViewModel.kt
// ============================================================================
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.data.ImageUploadRepository
import com.newaura.bookish.features.post.data.dto.CreatePostRequest
import com.newaura.bookish.features.post.data.dto.ImageFile
import com.newaura.bookish.features.post.data.dto.PostData
import com.newaura.bookish.features.post.domain.CreatePostUseCase
import com.newaura.bookish.features.post.domain.UploadState
import com.newaura.bookish.features.post.domain.model.ImageFileMapper
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// COMMON: di/ImageUploadModule.kt
// ============================================================================
import com.newaura.bookish.features.post.data.NoOpImageUploadManager
import com.newaura.bookish.features.post.domain.ImageUploadManager
import org.koin.dsl.module

// ============================================================================
// COMMON: di/CreatePostModule.kt
// ============================================================================
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.post.data.ImageUploadRepository
import com.newaura.bookish.features.post.data.ImageUploadRepositoryImpl
import com.newaura.bookish.features.post.ui.CreatePostViewModel
import com.newaura.bookish.features.post.domain.CreatePostUseCase
import com.newaura.bookish.features.post.domain.ImageUploadManager
import org.koin.dsl.module

// ============================================================================
// COMMON: App.kt
// ============================================================================
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.newaura.bookish.core.Context
import com.newaura.bookish.core.SplashScreen
import com.newaura.bookish.core.common.AppTheme
import com.newaura.bookish.core.config.JwtConfig
import com.newaura.bookish.core.data.AppDataStoreRepositoryManager
import com.newaura.bookish.core.data.UserDataStoreImpl
import com.newaura.bookish.core.domain.AppDataStoreRepository
import com.newaura.bookish.core.domain.JwtTokenValidator
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.features.auth.di.authDataModule
import com.newaura.bookish.features.auth.di.authDomainModule
import com.newaura.bookish.features.auth.di.createAuthService
import com.newaura.bookish.features.auth.domain.PhoneAuthService
import com.newaura.bookish.features.feed.UserRepository
import com.newaura.bookish.features.feed.di.commonModule
import com.newaura.bookish.features.feed.di.feedDataModule
import com.newaura.bookish.features.home.di.homePresentationModule
import com.newaura.bookish.features.post.di.createPostModule
import com.newaura.bookish.features.post.di.imageUploadModule
import com.newaura.bookish.features.post.domain.FilePicker
import com.newaura.bookish.features.post.ui.FilePickerImpl
import com.newaura.bookish.features.search.di.searchModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// ============================================================================
// ANDROID: data/ImageUploadWorkManager.kt
// ============================================================================
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

// ============================================================================
// ANDROID: di/ImageUploadWorkManagerModule.kt
// ============================================================================
import com.newaura.bookish.features.post.data.ImageUploadWorkManager
import com.newaura.bookish.features.post.domain.ImageUploadManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

// ============================================================================
// ANDROID: BookishApp.kt
// ============================================================================
import android.app.Application
import com.newaura.bookish.features.post.di.firebaseStorageModule
import com.newaura.bookish.features.post.di.imageUploadWorkManagerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

// ============================================================================
// COMPOSE UI: CreatePostScreen.kt (if you want to observe upload state)
// ============================================================================
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.newaura.bookish.features.post.domain.UploadState
import org.koin.compose.viewmodel.koinViewModel

// ============================================================================
// Types Used
// ============================================================================

/*
sealed class UploadState {
    object Idle : UploadState()
    data class Loading(val progress: Int = 0) : UploadState()
    data class Success(val uploadedUrls: List<String>) : UploadState()
    data class Error(val exception: Exception) : UploadState()
}

interface ImageUploadManager {
    fun scheduleImageUpload(imagePaths: List<String>, workTag: String = "image_upload"): String
    fun observeUploadState(workTag: String = "image_upload"): Flow<UploadState>
    fun cancelImageUpload(workTag: String = "image_upload")
    fun cancelImageUploadById(workId: String)
}

interface ImageUploadRepository {
    suspend fun uploadImage(imageFile: ImageFile): Flow<Result<String>>
    suspend fun uploadImages(imageFiles: List<ImageFile>): Flow<Result<List<String>>>
    suspend fun deleteImage(imageUrl: String): Result<Unit>
    fun scheduleBackgroundUpload(imagePaths: List<String>, workTag: String = "image_upload"): String
    fun observeUploadState(workTag: String = "image_upload"): Flow<UploadState>
    fun cancelUpload(workTag: String = "image_upload")
}
*/

// ============================================================================
// Package Structure
// ============================================================================

/*
composeApp/
├── src/
│   ├── commonMain/kotlin/com/newaura/bookish/
│   │   ├── features/post/
│   │   │   ├── domain/
│   │   │   │   ├── ImageUploadManager.kt          ✨ NEW
│   │   │   │   └── ... (existing files)
│   │   │   ├── data/
│   │   │   │   ├── ImageUploadRepository.kt       📝 MODIFIED
│   │   │   │   ├── NoOpImageUploadManager.kt      ✨ NEW
│   │   │   │   └── ... (existing files)
│   │   │   ├── ui/
│   │   │   │   ├── CreatePostViewModel.kt         📝 MODIFIED
│   │   │   │   └── ... (existing files)
│   │   │   └── di/
│   │   │       ├── ImageUploadModule.kt           ✨ NEW
│   │   │       ├── CreatePostModule.kt            📝 MODIFIED
│   │   │       └── ... (existing files)
│   │   └── App.kt                                  📝 MODIFIED
│   │
│   ├── androidMain/kotlin/com/newaura/bookish/
│   │   ├── features/post/
│   │   │   ├── data/
│   │   │   │   ├── ImageUploadWorkManager.kt      📝 MODIFIED
│   │   │   │   └── ... (existing files)
│   │   │   └── di/
│   │   │       ├── ImageUploadWorkManagerModule.kt 📝 MODIFIED
│   │   │       └── ... (existing files)
│   │   └── BookishApp.kt                          📝 MODIFIED
│   │
│   └── (iosMain, jsMain, etc.)
│       └── Coming soon...
│
└── build.gradle.kts                     (Add androidx.work:work-runtime-ktx:2.8.1)
*/

