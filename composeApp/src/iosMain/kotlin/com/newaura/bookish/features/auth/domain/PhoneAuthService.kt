package com.newaura.bookish.features.auth.domain

import com.newaura.bookish.core.ActivityContext

actual class PhoneAuthService {

    actual fun sendVerificationCode(
        activityContext: ActivityContext,
        phoneNumber: String,
        callback: (Result<String>) -> Unit
    ) {
    }

    actual fun verifyCode(code: String, callback: (Result<Boolean>) -> Unit) {
    }

}