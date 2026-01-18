package com.newaura.bookish

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class BookishApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@BookishApp)
            androidLogger()
            modules()
        }
    }
}