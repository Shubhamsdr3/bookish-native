package com.newaura.bookish

import android.app.Application
import com.newaura.bookish.features.post.di.firebaseStorageModule
import com.newaura.bookish.features.post.di.imageUploadWorkManagerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class BookishApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(this) {
            androidContext(this@BookishApp)
            androidLogger()
            modules(
                firebaseStorageModule,
                imageUploadWorkManagerModule
            )
        }
    }
}