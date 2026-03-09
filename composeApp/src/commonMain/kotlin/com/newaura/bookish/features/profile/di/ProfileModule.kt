package com.newaura.bookish.features.profile.di

import com.newaura.bookish.features.profile.domain.GetProfileDetailUseCase
import com.newaura.bookish.features.profile.ui.ProfileViewModel
import org.koin.dsl.module

val profileModule = module {
    factory { GetProfileDetailUseCase(get()) }
    factory { ProfileViewModel(get(), get()) }
}