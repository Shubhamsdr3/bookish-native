package com.newaura.bookish.core.domain

import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UserState : KoinComponent {

    var currentUserId: String = ""
        private set

    private val userDataStore: UserDataStore by inject()

    suspend fun init() {
        currentUserId = userDataStore.currentUserId.first()
    }

    fun clearUserState() {
        currentUserId = ""
    }
}