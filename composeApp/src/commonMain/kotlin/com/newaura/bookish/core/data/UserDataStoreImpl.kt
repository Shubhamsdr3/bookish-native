package com.newaura.bookish.core.data

import com.newaura.bookish.core.domain.AppDataStoreRepository
import com.newaura.bookish.core.domain.DataStoreKeys
import com.newaura.bookish.core.domain.UserDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserDataStoreImpl(
    private val appDataStoreRepository: AppDataStoreRepository,
) : UserDataStore {

  override val currentUserId: Flow<String>
      get() = flow {
          emit(appDataStoreRepository.readValue(DataStoreKeys.USER_ID) ?: "")
      }
}