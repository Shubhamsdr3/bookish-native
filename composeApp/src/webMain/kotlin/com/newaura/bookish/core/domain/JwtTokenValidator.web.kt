package com.newaura.bookish.core.domain

import com.newaura.bookish.core.data.JwtClaims
import com.newaura.bookish.core.data.JwtValidationResult

actual object JwtTokenValidator {
    actual fun setPublicKey(publicKeyPem: String) {
    }

    actual fun validate(token: String): JwtValidationResult {
        TODO("Not yet implemented")
    }

    actual fun isExpired(token: String): Boolean {
        TODO("Not yet implemented")
    }

    actual fun extractClaims(token: String): JwtClaims? {
        TODO("Not yet implemented")
    }

    actual fun getExpirationTimeInSeconds(token: String): Long? {
        TODO("Not yet implemented")
    }

    actual fun getRemainingValidityInSeconds(token: String): Long? {
        TODO("Not yet implemented")
    }
}