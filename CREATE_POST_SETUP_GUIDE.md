# CreatePostViewModel - KMP Implementation Setup Guide

This guide explains how to fully integrate the `CreatePostViewModel` in your KMP project based on the Flutter `CreatePostController`.

## Files Created

### 1. **CreatePostViewModel.kt** (Updated)
- Main ViewModel with all state management logic
- Mirrors Flutter CreatePostController functionality
- Uses StateFlow for reactive state management

### 2. **CreatePostUiState.kt** (Updated)
- UI state definitions (Idle, Loading, Success, Error, NavigateToSearch, NavigateToHome)
- ScreenState data class with form fields and validation state

### 3. **ImagePickerExpect.kt** (New)
- Expect declarations for multiplatform image picking
- Defines interfaces for image/video selection
- Common abstractions for platform-specific code

### 4. **ImagePickerActual.kt** (Android)
- Android-specific image picker implementation
- Uses Android Activity Result APIs
- Helper functions for Uri to ByteArray conversion

### 5. **ImagePickerActual.kt** (iOS)
- iOS-specific image picker implementation
- PHPickerViewController and UIImagePickerController integration
- Video duration handling

### 6. **CreatePostScreenCompose.kt** (Example)
- Complete Compose UI implementation
- Shows how to wire ViewModel with UI
- Example of state collection and navigation

## Integration Steps

### Step 1: Update Build Configuration

Add Firebase dependencies to your `composeApp/build.gradle.kts`:

```kotlin
dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.x.x"))
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    
    // Image loading
    implementation("io.coil-kt.coil3:coil-compose:3.0.x")
    
    // DI (if using Koin)
    implementation("io.insert-koin:koin-android:3.x.x")
    implementation("io.insert-koin:koin-compose:1.x.x")
}
```

### Step 2: Set Up Dependency Injection

Create a module for CreatePostViewModel:

```kotlin
// di/PostModule.kt
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val postModule = module {
    single { CreatePostUseCase(get()) }
    viewModel { CreatePostViewModel(get()) }
}
```

### Step 3: Implement FirebaseStorage Upload

Complete the `uploadImageFile()` method:

```kotlin
private suspend fun uploadImageFile(imageFile: ImageFile): String {
    return try {
        val storageRef = Firebase.storage.reference
            .child("post/images/${imageFile.name}")
        
        val uploadTask = storageRef.putBytes(imageFile.byteArray)
        val snapshot = Tasks.await(uploadTask)
        
        return Tasks.await(snapshot.storage.downloadUrl).toString()
    } catch (exception: Exception) {
        throw Exception("Error uploading image: ${exception.message}")
    }
}
```

### Step 4: Implement UserStore for KMP

Create a common UserStore:

```kotlin
// common/UserStore.kt
expect object UserStore {
    suspend fun getUserId(): String
    suspend fun saveUserId(userId: String)
    suspend fun getUserToken(): String
}
```

**Android implementation:**
```kotlin
// androidMain/UserStore.kt
actual object UserStore {
    private val context: Context = // Get from DI
    private val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    
    actual suspend fun getUserId(): String {
        return prefs.getString("userId", "") ?: ""
    }
    
    actual suspend fun saveUserId(userId: String) {
        prefs.edit().putString("userId", userId).apply()
    }
    
    actual suspend fun getUserToken(): String {
        return prefs.getString("token", "") ?: ""
    }
}
```

**iOS implementation:**
```kotlin
// iosMain/UserStore.kt
actual object UserStore {
    actual suspend fun getUserId(): String {
        // Use UserDefaults or Keychain
        return "" // Placeholder
    }
    
    actual suspend fun saveUserId(userId: String) {
        // Save to UserDefaults or Keychain
    }
    
    actual suspend fun getUserToken(): String {
        return ""
    }
}
```

### Step 5: Complete Android Image Picker Implementation

Update `androidMain/ImagePickerActual.kt`:

```kotlin
actual suspend fun pickImagesFromGallery(quality: Int): List<PlatformImageFile> {
    // This should be called from a Composable using Activity Result API
    // Example in Composable:
    val pickImages = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            val bytes = uri.readBytes(context)
            val fileName = uri.getFileName(context)
            viewModel.onGalleryImageSelected(listOf(
                ImageFile(fileName, uri.toString(), bytes)
            ))
        }
    }
    
    pickImages.launch("image/*")
    return emptyList()
}
```

### Step 6: Set Up Navigation

In your composable, handle navigation states:

```kotlin
@Composable
fun CreatePostScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel: CreatePostViewModel = koinViewModel()
    val uiState by viewModel.postScreenState.collectAsState()
    
    LaunchedEffect(uiState) {
        when (uiState.uiState) {
            is CreatePostUiState.Success -> {
                navigator.pop()
                // Show success toast
            }
            is CreatePostUiState.NavigateToSearch -> {
                // navigator.push(SearchSuggestionScreen())
            }
            is CreatePostUiState.Error -> {
                val error = uiState.uiState as CreatePostUiState.Error
                // showToast(error.message)
            }
            else -> {}
        }
    }
    
    // UI Code...
}
```

### Step 7: Implement Image Picker in Compose

Create an image picker helper composable:

```kotlin
@Composable
fun ImagePickerDialog(
    onImagesSelected: (List<ImageFile>) -> Unit,
    onDismiss: () -> Unit
) {
    // Show options: Camera, Gallery, Video
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Handle captured image
            onImagesSelected(listOf(/* imageFile */))
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val imageFiles = uris.map { uri ->
            ImageFile(
                name = uri.getFileName(context),
                path = uri.toString(),
                byteArray = uri.readBytes(context)
            )
        }
        onImagesSelected(imageFiles)
    }
    
    // Show dialog with options...
}
```

## Key Implementation Points

### 1. **State Management**
- Use StateFlow for all mutable state
- Update state using `.update()` for thread-safety
- Collect state in Compose using `.collectAsState()`

### 2. **Image Handling**
- Image picking happens in Composable using Activity Result APIs
- ViewModel only manages the list of selected images
- Platform-specific image compression/optimization should happen in expect/actual

### 3. **Firebase Upload**
- Use Firebase Kotlin SDK
- Upload happens in a suspend function
- Use `Tasks.await()` to convert Task to coroutine

### 4. **Error Handling**
- Specific error types (Unauthorized, Network, etc.)
- Navigation is state-based, not direct navigation calls
- Composable observes state and handles navigation

### 5. **Form Validation**
- Button enabled state is managed in ViewModel
- Validation rules: bookTitle and postCaption must not be empty
- Update validation when text changes

## Common Issues and Solutions

### Issue 1: "CompositionLocal is null" Error
**Solution**: Don't access Navigator in ViewModel. Use state-based navigation.

### Issue 2: Images not uploading
**Solution**: Ensure Firebase is properly initialized and google-services.json is in correct location.

### Issue 3: Lost state on configuration change
**Solution**: StateFlow in ViewModel persists through configuration changes. ✓

### Issue 4: Image quality issues
**Solution**: Implement compression in the expect/actual image picker functions.

## Testing Checklist

- [ ] Create post with all fields
- [ ] Validate button is disabled when fields are empty
- [ ] Upload multiple images successfully
- [ ] Handle network errors gracefully
- [ ] Clear form after successful post creation
- [ ] Navigation to search screen works
- [ ] Navigation back to home after success works
- [ ] Handle unauthorized/session expired errors

## Related Files

- `CreatePostUseCase.kt` - Business logic layer
- `FeedRepository.kt` - Data layer
- `FeedData.kt` - Data models
- `PostType.kt` - Enum for post types

## Resources

- [Firebase Kotlin SDK](https://firebase.google.com/docs/android/setup)
- [Compose State Management](https://developer.android.com/jetpack/compose/state)
- [KMP Expect/Actual](https://kotlinlang.org/docs/multiplatform-expect-actual.html)
- [Activity Results API](https://developer.android.com/training/basics/intents/result)

## Next Steps

1. Complete Firebase initialization
2. Implement UserStore for KMP
3. Complete image picker implementations
4. Set up proper DI with Koin
5. Add error toast/dialog handling
6. Implement search navigation
7. Add unit tests for ViewModel
8. Add UI tests for Composables

