# CreatePostViewModel - Flutter to KMP Migration

This document explains how the Flutter `CreatePostController` has been ported to KMP `CreatePostViewModel`.

## Flutter vs KMP Implementation Mapping

### 1. **State Management**

#### Flutter (GetX)
```dart
RxList<XFile> imageFiles = <XFile>[].obs;
RxBool isLoading = false.obs;
Rx<ButtonState> buttonState = ButtonState.disabled.obs;
```

#### KMP (StateFlow)
```kotlin
private val _selectedImages = MutableStateFlow<List<ImageFile>>(emptyList())
private val _isLoading = MutableStateFlow(false)
private val _buttonState = MutableStateFlow(ButtonState.DISABLED)
```

**Difference**: KMP uses `StateFlow` (Kotlin Coroutines) instead of Rx reactive variables.

---

### 2. **Text Controllers**

#### Flutter
```dart
final TextEditingController postController = TextEditingController();
final TextEditingController bookTitleController = TextEditingController();
final TextEditingController linkController = TextEditingController();
```

#### KMP
```kotlin
private val _postCaption = MutableStateFlow("")
private val _bookTitle = MutableStateFlow("")
private val _bookLink = MutableStateFlow("")
```

**Difference**: KMP uses `MutableStateFlow` for text values instead of `TextEditingController` (which is specific to Flutter/Android Views). For Compose, we can create extension functions to convert StateFlow to TextFieldValue if needed.

---

### 3. **Listeners/Observers**

#### Flutter
```dart
void setListener() {
  postController.addListener(() {
    enablePostButton();
  });
  bookTitleController.addListener(() {
    enablePostButton();
  });
}
```

#### KMP
```kotlin
// In Compose, we use snapshotFlow or LaunchedEffect
LaunchedEffect(postCaption, bookTitle) {
    viewModel.enablePostButton()
}

// Or in ViewModel with snapshotFlow (if needed)
init {
    viewModelScope.launch {
        snapshotFlow { 
            postCaption.value to bookTitle.value 
        }.collect {
            enablePostButton()
        }
    }
}
```

**Implementation Note**: In Compose UI, the listener is better handled in the composable's LaunchedEffect rather than in the ViewModel.

---

### 4. **Image Picking and Management**

#### Flutter
```dart
final ImagePicker _imagePicker = ImagePicker();

Future<void> galleryClicked(ImageSource imageSource) async {
  Get.back();
  final pickedFiles = await _imagePicker.pickMultiImage(imageQuality: 25);
  imageFiles.addAll(pickedFiles);
}
```

#### KMP
```kotlin
fun addImages(images: List<ImageFile>) {
    _selectedImages.update { currentImages ->
        currentImages + images
    }
}

fun removeImage(index: Int) {
    _selectedImages.update { currentImages ->
        currentImages.filterIndexed { i, _ -> i != index }
    }
}
```

**Difference**: 
- Flutter handles image picking in the Controller
- KMP separates concerns: image picking happens in the UI/Composable, ViewModel just manages the list
- For KMP, use multiplatform libraries like `Calf` or platform-specific expect/actual declarations

---

### 5. **File Upload to Firebase**

#### Flutter
```dart
Future<String> _uploadImageFile(XFile imageFile) async {
  try {
    final file = File(imageFile.path);
    var storageRef = FirebaseStorage.instance.ref("post/images/${imageFile.name}");
    final TaskSnapshot snapshot = await storageRef.putFile(file);
    String downloadUrl = await snapshot.ref.getDownloadURL();
    return downloadUrl;
  } catch (ex) {
    rethrow;
  }
}
```

#### KMP
```kotlin
private suspend fun uploadImageFile(imageFile: ImageFile): String {
    return try {
        // Firebase Kotlin SDK
        val storageRef = Firebase.storage.reference.child("post/images/${imageFile.name}")
        val uploadTask = storageRef.putBytes(imageFile.byteArray)
        val snapshot = uploadTask.await()
        return snapshot.storage.getDownloadUrl().await().toString()
    } catch (exception: Exception) {
        throw Exception("Error uploading image: ${exception.message}")
    }
}
```

**Note**: Ensure `firebase-storage` dependency is added to your build.gradle.kts:
```kotlin
implementation("com.google.firebase:firebase-storage-ktx:20.x.x")
```

---

### 6. **Create Post API Call**

#### Flutter
```dart
_repository.createPost(requestBody).then((response) {
  isLoading.value = false;
  AppState().feedData.value = FeedData.fromJson(response.data['data']);
  Get.off(HomeScreen());
  AppState().tabIndex.value = 0;
}).onError((e, s) {
  isLoading.value = false;
  if (e is UnAuthorizedException) {
    Get.toNamed(LoginScreen.routeName);
  } else {
    debugPrint("Bookish: $e");
  }
});
```

#### KMP
```kotlin
createPostUseCase.invoke(
    caption = requestBody.post.caption,
    images = requestBody.post.images
).collect { result ->
    result.onSuccess { feedData ->
        _isLoading.update { false }
        _postScreenState.update { 
            it.copy(
                uiState = CreatePostUiState.Success("Post created successfully")
            )
        }
        clearForm()
    }
    result.onFailure { exception ->
        _isLoading.update { false }
        handleError(exception)
    }
}
```

**Difference**:
- Flutter uses `.then()` and `.onError()` callbacks
- KMP uses `Flow.collect()` with `Result` type
- Navigation is handled by the Composable observing the state, not by the ViewModel directly

---

### 7. **Error Handling**

#### Flutter
```dart
void handleError(Exception e) {
  if (e is UnAuthorizedException) {
    Get.toNamed(LoginScreen.routeName);
  } else {
    debugPrint("Bookish: $e");
  }
}
```

#### KMP
```kotlin
private fun handleError(exception: Exception) {
    val errorMessage = when {
        exception.message?.contains("Unauthorized", ignoreCase = true) == true -> {
            // Composable will handle navigation based on state
            "Session expired. Please login again."
        }
        exception.message?.contains("Network", ignoreCase = true) == true -> {
            "Network error. Please check your connection."
        }
        else -> exception.message ?: "Unknown error occurred"
    }

    _postScreenState.update { 
        it.copy(
            uiState = CreatePostUiState.Error(errorMessage)
        )
    }
}
```

**Difference**: Navigation is not done directly from the ViewModel. Instead, the Composable observes the state and handles navigation accordingly.

---

### 8. **Form Clearing**

#### Flutter
```dart
void onClose() {
  postController.dispose();
  bookTitleController.clear();
  bookTitleController.dispose();
  linkController.dispose();
  imageFiles.clear();
  super.onClose();
}
```

#### KMP
```kotlin
private fun clearForm() {
    _postCaption.update { "" }
    _bookTitle.update { "" }
    _bookLink.update { "" }
    _selectedImages.update { emptyList() }
    _selectedBook.update { null }
    _selectedPostType.update { PostType.REVIEW }
    _buttonState.update { ButtonState.DISABLED }
}

override fun onCleared() {
    clearForm()
    super.onCleared()
}
```

---

### 9. **Button Enable/Disable Logic**

#### Flutter
```dart
void enablePostButton() {
  if (!isLoading.value && 
      bookTitleController.text.isNotEmpty && 
      postController.text.isNotEmpty) {
    buttonState.value = ButtonState.enabled;
  } else {
    buttonState.value = ButtonState.disabled;
  }
}
```

#### KMP
```kotlin
private fun enablePostButton() {
    val isButtonEnabled = !_isLoading.value && 
        _bookTitle.value.isNotEmpty() && 
        _postCaption.value.isNotEmpty()

    _buttonState.update { 
        if (isButtonEnabled) ButtonState.ENABLED else ButtonState.DISABLED
    }
}
```

---

## Key Differences: Flutter vs KMP Architecture

| Aspect | Flutter | KMP |
|--------|---------|-----|
| **State Management** | GetX (RxBool, RxList) | StateFlow |
| **Text Input** | TextEditingController | MutableStateFlow<String> |
| **Async Handling** | Future/.then()/.onError() | Flow + viewModelScope.launch |
| **Navigation** | Get.toNamed() in Controller | Composable observes state |
| **Dependency Injection** | Get.put() | Koin or manual DI |
| **Lifecycle** | GetxController.onClose() | ViewModel.onCleared() |
| **Image Picking** | In Controller | In Composable using expect/actual |

---

## Usage in Compose UI

```kotlin
@Composable
fun CreatePostScreen() {
    val viewModel: CreatePostViewModel = koinViewModel()
    val postCaption by viewModel.postCaption.collectAsState()
    val bookTitle by viewModel.bookTitle.collectAsState()
    val buttonState by viewModel.buttonState.collectAsState()
    val uiState by viewModel.postScreenState.collectAsState()

    // Listen for caption changes
    LaunchedEffect(postCaption) {
        // Recompose UI when caption changes
    }

    // Handle state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is CreatePostUiState.Success -> {
                // Navigate to home
                navigator.pop()
            }
            is CreatePostUiState.Error -> {
                // Show error toast
            }
            else -> {}
        }
    }

    Column {
        OutlinedTextField(
            value = postCaption,
            onValueChange = viewModel::updatePostCaption,
            placeholder = { Text("What do you think?") }
        )

        Button(
            onClick = viewModel::createPost,
            enabled = buttonState == ButtonState.ENABLED
        ) {
            Text("Post")
        }
    }
}
```

---

## Next Steps

1. **Implement Image Picker**: Create expect/actual declarations for multiplatform image picking
2. **Firebase Integration**: Add Firebase Auth and Storage dependencies
3. **UserStore**: Implement UserStore for KMP to get user ID
4. **Navigation**: Set up navigation based on UI state changes
5. **Testing**: Add unit tests for ViewModel logic

---

## References

- [Kotlin StateFlow Documentation](https://kotlinlang.org/docs/flow.html#stateflow)
- [Android ViewModel Guide](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Firebase Kotlin SDK](https://firebase.google.com/docs/android/setup)
- [Jetpack Compose State Management](https://developer.android.com/jetpack/compose/state)

