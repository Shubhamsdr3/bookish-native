package com.newaura.bookish.features.post.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.domain.ImageUploadRepository
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
import kotlin.time.Clock

class CreatePostViewModel(
    private val createPostUseCase: CreatePostUseCase,
    private val userDataStore: UserDataStore,
    private val imageUploadRepository: ImageUploadRepository
) : ViewModel() {

    private val _postScreenState = MutableStateFlow(CreatePostScreenState())
    val postScreenState: StateFlow<CreatePostScreenState> = _postScreenState.asStateFlow()

    private val _postUiDataState = MutableStateFlow<CreatePostUiState>(CreatePostUiState.Idle)
    val postUiDataState: StateFlow<CreatePostUiState> = _postUiDataState.asStateFlow()

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    fun updateUiState(postScreenState: CreatePostScreenState) {
        _postScreenState.update { postScreenState }
    }

    fun addGalleryImages(photos: List<GalleryPhotoResult>) {
        val images = photos.map { ImageFileMapper.mapGalleryPhotoToImageFile(it) }
        _postScreenState.update { currentState ->
            val newImages = currentState.selectedImages + images
            currentState.copy(
                selectedImages = newImages
            )
        }
    }

    fun addCameraImage(photo: PhotoResult) {
        val image = ImageFileMapper.mapPhotoToImageFile(photo)
        _postScreenState.update { currentState ->
            val newImages = currentState.selectedImages + image
            currentState.copy(
                selectedImages = newImages
            )
        }
    }

    fun removeImage(imageFile: ImageFile) {
        _postScreenState.update { currentState ->
            currentState.copy(
                selectedImages = currentState.selectedImages.filter { it != imageFile }
            )
        }
    }

    fun createPost() {
        val userId = userDataStore.currentUserId
        if (userId.isNullOrEmpty()) {
            _postUiDataState.value = CreatePostUiState.Error("User not authenticated")
            return
        }

        if (_postScreenState.value.selectedBook == null) {
            _postUiDataState.value = CreatePostUiState.Error("Please select a book")
            return
        }

        if (_postScreenState.value.postCaption.isEmpty()) {
            _postUiDataState.value = CreatePostUiState.Error("Please add your thoughts")
            return
        }

        viewModelScope.launch {
            _postUiDataState.value = CreatePostUiState.Loading

            try {
                // Upload images using WorkManager in background
                val selectedImages = _postScreenState.value.selectedImages
                if (selectedImages.isNotEmpty()) {
                    uploadImagesInBackground(selectedImages, userId)
                } else {
                    // No images, create post immediately
                    createPostWithUrls(userId, emptyList())
                }
            } catch (exception: Exception) {
                AppLogger.e(exception)
                exception.printStackTrace()
                handleError(exception)
            }
        }
    }

    /**
     * Schedule images for background upload using WorkManager
     * @param imageFiles List of images to upload
     * @param userId User ID for post creation
     */
    private fun uploadImagesInBackground(imageFiles: List<ImageFile>, userId: String) {
        val imageUris = imageFiles.map { it.contentUri }
        val workTag = "post_${Clock.System.now()}"

        // Schedule the upload work
        imageUploadRepository.scheduleBackgroundUpload(imageUris, workTag)

        // Observe upload state
        viewModelScope.launch {
            imageUploadRepository.observeUploadState(workTag).collect { state ->
                _uploadState.value = state

                when (state) {
                    is UploadState.Success -> {
                        AppLogger.d("✅ Upload complete with ${state.uploadedUrls.size} URLs")
                        createPostWithUrls(userId, state.uploadedUrls)
                    }

                    is UploadState.Error -> {
                        AppLogger.e("❌ Upload error: ${state.exception.message}")
                        handleError(state.exception)
                    }

                    is UploadState.Loading -> {
                        AppLogger.d("⏳ Upload in progress...")
                        _postUiDataState.value = CreatePostUiState.Loading
                    }

                    UploadState.Idle -> {
                        AppLogger.d("Idle state")
                    }
                }
            }
        }
    }

    /**
     * Create post with uploaded image URLs
     * @param userId User ID
     * @param uploadedImageUrls List of uploaded image URLs
     */
    private fun createPostWithUrls(userId: String, uploadedImageUrls: List<String>) {
        viewModelScope.launch {
            try {
                val bookCategories =
                    _postScreenState.value.selectedBook?.volumeInfo?.categories?.map { it }
                        ?: emptyList()

                val requestBody = CreatePostRequest(
                    userId = userId,
                    isLiked = false,
                    post = PostData(
                        caption = _postScreenState.value.postCaption,
                        images = uploadedImageUrls,
                        bookName = _postScreenState.value.selectedBook?.volumeInfo?.title ?: "",
                        bookLink = _postScreenState.value.bookLink,
                        bookId = _postScreenState.value.selectedBook?.id ?: "",
                        bookCategories = bookCategories,
                        postType = _postScreenState.value.selectedPostType.name.lowercase()
                    )
                )

                createPostUseCase.invoke(requestBody).collect { result ->
                    result.onSuccess {
                        _postUiDataState.value =
                            CreatePostUiState.Success("Post created successfully")
                        clearForm()
                    }
                    result.onFailure { exception ->
                        handleError(exception)
                    }
                }
            } catch (exception: Exception) {
                AppLogger.e(exception)
                exception.printStackTrace()
                handleError(exception)
            }
        }
    }


    private fun handleError(exception: Throwable) {
        val errorMessage = when {
            exception.message?.contains("Unauthorized", ignoreCase = true) == true ->
                "Session expired. Please login again."

            exception.message?.contains("Network", ignoreCase = true) == true ->
                "Network error. Please check your connection."

            else -> exception.message ?: "Unknown error occurred"
        }

        _postUiDataState.value = CreatePostUiState.Error(errorMessage)
    }

    private fun clearForm() {
        _postScreenState.update {
            CreatePostScreenState()
        }
    }
}