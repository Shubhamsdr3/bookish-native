package com.newaura.bookish.features.auth.di

import com.newaura.bookish.core.ApplicationContext
import com.newaura.bookish.core.Context
import com.newaura.bookish.features.auth.domain.PhoneAuthService

actual fun createAuthService(context: ApplicationContext): PhoneAuthService = PhoneAuthService()