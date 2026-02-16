package com.newaura.bookish.core.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JwtClaims(
    @SerialName("iss")
    val issuer: String? = null,
    @SerialName("sub")
    val subject: String? = null,
    @SerialName("aud")
    val audience: String? = null,
    @SerialName("exp")
    val expirationTime: Long? = null,
    @SerialName("nbf")
    val notBefore: Long? = null,
    @SerialName("iat")
    val issuedAt: Long? = null,
    @SerialName("jti")
    val jwtId: String? = null,
    @SerialName("userId")
    val userId: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,
)

@Serializable
data class JwtToken(
    val header: Map<String, String>,
    val claims: JwtClaims,
    val signature: String,
)

sealed class JwtValidationResult {
    data class Valid(val claims: JwtClaims) : JwtValidationResult()
    data class Invalid(val reason: String) : JwtValidationResult()
    data class Expired(val expirationTime: Long) : JwtValidationResult()
    data class MalformedToken(val reason: String) : JwtValidationResult()
}