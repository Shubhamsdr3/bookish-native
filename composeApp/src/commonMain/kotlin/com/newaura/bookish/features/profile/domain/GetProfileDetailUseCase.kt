package com.newaura.bookish.features.profile.domain

import com.newaura.bookish.features.feed.UserRepository
import com.newaura.bookish.features.profile.data.ProfileResponse
import kotlinx.coroutines.flow.Flow

class GetProfileDetailUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(userId: String): Flow<Result<ProfileResponse?>> {
        return userRepository.getUserProfile(userId)
    }
}