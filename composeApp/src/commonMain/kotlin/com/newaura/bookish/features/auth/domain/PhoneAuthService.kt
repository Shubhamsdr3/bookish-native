package com.newaura.bookish.features.auth.domain

import com.newaura.bookish.core.ActivityContext

expect class PhoneAuthService {
    fun sendVerificationCode(activityContext: ActivityContext, phoneNumber: String, callback: (Result<String>) -> Unit)
    fun verifyCode(code: String, callback: (Result<Boolean>) -> Unit)
}
