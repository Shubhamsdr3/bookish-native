package com.newaura.bookish.features.post.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newaura.bookish.core.domain.UserDataStore
import com.newaura.bookish.core.util.AppLogger
import com.newaura.bookish.features.post.data.ImageUploadRepository
import com.newaura.bookish.features.post.data.dto.CreatePostRequest
import com.newaura.bookish.features.post.data.dto.ImageFile
import com.newaura.bookish.features.post.data.dto.PostData
import com.newaura.bookish.features.post.domain.CreatePostUseCase
import com.newaura.bookish.features.post.domain.model.ImageFileMapper
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatePostViewModel(
    private val createPostUseCase: CreatePostUseCase,
    private val userDataStore: UserDataStore,
    private val imageUploadRepository: ImageUploadRepository
) : ViewModel() {

    private val _postScreenState = MutableStateFlow(CreatePostScreenState())
    val postScreenState: StateFlow<CreatePostScreenState> = _postScreenState.asStateFlow()

    private val _postUiDataState = MutableStateFlow<CreatePostUiState>(CreatePostUiState.Idle)
    val postUiDataState: StateFlow<CreatePostUiState> = _postUiDataState.asStateFlow()

    private val _selectedPostTypeIndex = MutableStateFlow(0)
    val selectedPostTypeIndex: StateFlow<Int> = _selectedPostTypeIndex.asStateFlow()

    fun updateUiState(postScreenState: CreatePostScreenState) {
        _selectedPostTypeIndex.update { postScreenState.selectedPostType.ordinal }
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
                // Upload images to Firebase Storage
                val selectedImages = _postScreenState.value.selectedImages
                val uploadedImageUrls = if (selectedImages.isNotEmpty()) {
                    uploadImagesToFirebase(selectedImages)
                } else {
                    emptyList()
                }

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

    /**
     * Upload images to Firebase Storage
     * @param imageFiles List of images to upload
     * @return List of download URLs from Firebase
     * @throws Exception if upload fails
     */
    private suspend fun uploadImagesToFirebase(imageFiles: List<ImageFile>): List<String> {
        return try {
            val uploadedUrls = mutableListOf<String>()
            imageUploadRepository.uploadImages(imageFiles).collect { result ->
                result.onSuccess { urls ->
                    uploadedUrls.addAll(urls)
                }.onFailure { exception ->
                    AppLogger.e(exception)
                    throw exception
                }
            }

            uploadedUrls
        } catch (exception: Exception) {
            AppLogger.e(exception)
            exception.printStackTrace()
            throw Exception("Failed to upload images: ${exception.message}")
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