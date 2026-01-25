package com.newaura.bookish.core.data

import com.newaura.bookish.core.Context
import com.newaura.bookish.core.domain.AppDataStoreRepository

const val APP_DATASTORE = "com.newaura.bookish"

class AppDataStoreRepositoryManager(val context: Context?) : AppDataStoreRepository {

    override suspend fun setValue(
        key: String,
        value: String
    ) {
        context.putData(key, value)
    }

    override suspend fun readValue(
        key: String,
    ): String? {
        return context.getData(key)
    }
}

expect suspend fun Context?.putData(key: String, `object`: String)

expect suspend fun Context?.getData(key: String): String?