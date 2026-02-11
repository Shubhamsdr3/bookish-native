package com.newaura.bookish.core.domain

import kotlinx.coroutines.flow.Flow

interface UserDataStore {

    val currentUserId: Flow<String>
}