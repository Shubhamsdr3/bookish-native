package com.newaura.bookish.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.newaura.bookish.core.ApplicationContext
import kotlinx.coroutines.flow.first

val ApplicationContext.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)

actual suspend fun ApplicationContext?.getData(key: String): String? {
    return this?.dataStore?.data?.first()[stringPreferencesKey(key)] ?: ""
}

actual suspend fun ApplicationContext?.putData(key: String, `object`: String) {
    this?.dataStore?.edit {
        it[stringPreferencesKey(key)] = `object`
    }
}