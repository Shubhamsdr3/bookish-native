package com.newaura.bookish.features.auth.domain

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.newaura.bookish.core.ActivityContext
import java.util.concurrent.TimeUnit

actual class PhoneAuthService {

    private val auth = FirebaseAuth.getInstance()
    private var _verificationId: String = ""

    actual fun sendVerificationCode(
        activityContext: ActivityContext,
        phoneNumber: String,
        callback: (Result<String>) -> Unit
    ) {
        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+91$phoneNumber")
                .setActivity(activityContext)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        print("Shubham ==> onVerificationCompleted: $p0")
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        super.onCodeSent(verificationId, token)
                        println("Shubham ==> onCodeSent: $verificationId")
                        _verificationId = verificationId
                        callback(Result.success(verificationId))
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        // IMPORTANT: Handle reCAPTCHA fallback error here
                        print("Shubham ==> onVerificationCompleted: $e")
                        if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                            // This means a reCAPTCHA was needed but no activity was provided.
                            // The activity is now passed in the function call, so this case
                            // should be less frequent unless the activity is finished.
                        }
                        callback(Result.failure(e))
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            callback(Result.failure(e))
        }
    }

    actual fun verifyCode(
        code: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        try {
            val credential = PhoneAuthProvider.getCredential(_verificationId, code)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    callback(Result.success(task.isSuccessful))
                }
        } catch (e: Exception) {
            callback(Result.failure(e))
        }
    }
}
