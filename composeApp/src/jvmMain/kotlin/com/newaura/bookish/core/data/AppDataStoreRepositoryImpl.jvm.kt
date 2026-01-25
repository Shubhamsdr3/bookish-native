package com.newaura.bookish.core.data

import com.newaura.bookish.core.Context

actual suspend fun Context?.putData(
    key: String,
    `object`: String
) {
}

actual suspend fun Context?.getData(key: String): String? {
    TODO("Not yet implemented")
}