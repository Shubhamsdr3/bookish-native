package com.newaura.bookish.core.domain

interface AppDataStoreRepository {

    suspend fun setValue(
        key: String,
        value: String
    )

    suspend fun readValue(
        key: String,
    ): String?

}