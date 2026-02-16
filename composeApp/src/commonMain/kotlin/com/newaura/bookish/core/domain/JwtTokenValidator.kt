package com.newaura.bookish.core.domain

import com.newaura.bookish.core.data.JwtClaims
import com.newaura.bookish.core.data.JwtValidationResult

expect object JwtTokenValidator {
    fun setPublicKey(publicKeyPem: String)
    fun validate(token: String): JwtValidationResult
    fun isExpired(token: String): Boolean
    fun extractClaims(token: String): JwtClaims?
    fun getExpirationTimeInSeconds(token: String): Long?
    fun getRemainingValidityInSeconds(token: String): Long?
}