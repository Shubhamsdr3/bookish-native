package com.newaura.bookish.core.domain

import com.newaura.bookish.core.data.JwtClaims
import com.newaura.bookish.core.data.JwtValidationResult
import kotlinx.serialization.json.Json
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

actual object JwtTokenValidator {

    private val json = Json { ignoreUnknownKeys = true }
    private var publicKey: PublicKey? = null

    /**
     * Initialize with backend public key for signature verification
     * @param publicKeyPem PEM-formatted public key from backend
     */
    actual fun setPublicKey(publicKeyPem: String) {
        try {
            val publicKeyContent = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", "")

            val decoded = Base64.decode(publicKeyContent)
            val keySpec = X509EncodedKeySpec(decoded)
            val keyFactory = KeyFactory.getInstance("RSA")
            publicKey = keyFactory.generatePublic(keySpec)
        } catch (ex: Exception) {
            println("Failed to set public key: ${ex.message}")
        }
    }

    actual fun validate(token: String): JwtValidationResult {
        try {
            val parts = token.split(".")

            if (parts.size != 3) {
                return JwtValidationResult.MalformedToken(
                    "Invalid JWT format. Expected 3 parts, got ${parts.size}"
                )
            }

            val headerJson = decodeBase64(parts[0])
            val header = json.decodeFromString<Map<String, String>>(headerJson)

            val payloadJson = decodeBase64(parts[1])
            val claims = json.decodeFromString<JwtClaims>(payloadJson)

            val signature = parts[2]

            // Verify signature if public key is available
            if (publicKey != null) {
                if (!verifySignature(parts[0] + "." + parts[1], signature)) {
                    return JwtValidationResult.Invalid("Signature verification failed")
                }
            }

            // Check expiration
            val currentTimeInSeconds = kotlin.time.Clock.System.now().epochSeconds
            if (claims.expirationTime != null && claims.expirationTime <= currentTimeInSeconds) {
                return JwtValidationResult.Expired(claims.expirationTime)
            }

            // Check not before
            if (claims.notBefore != null && claims.notBefore > currentTimeInSeconds) {
                return JwtValidationResult.Invalid(
                    "Token is not yet valid. Valid from: ${claims.notBefore}"
                )
            }

            return JwtValidationResult.Valid(claims)
        } catch (ex: Exception) {
            return JwtValidationResult.MalformedToken(
                "Error validating token: ${ex.message}"
            )
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun verifySignature(signedContent: String, signatureB64: String): Boolean {
        return try {
            val signature = Signature.getInstance("SHA256withRSA")
            signature.initVerify(JwtTokenValidator.publicKey)
            signature.update(signedContent.toByteArray())

            val decodedSignature = Base64.UrlSafe.decode(signatureB64 + "=".repeat((4 - signatureB64.length % 4) % 4))
            signature.verify(decodedSignature)
        } catch (ex: Exception) {
            false
        }
    }

    actual fun isExpired(token: String): Boolean {
        return when (val result = validate(token)) {
            is JwtValidationResult.Expired -> true
            is JwtValidationResult.Valid -> {
                val currentTimeInSeconds = kotlin.time.Clock.System.now().epochSeconds
                result.claims.expirationTime?.let { it <= currentTimeInSeconds } ?: false
            }
            else -> true
        }
    }

    actual fun extractClaims(token: String): JwtClaims? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payloadJson = decodeBase64(parts[1])
            json.decodeFromString<JwtClaims>(payloadJson)
        } catch (ex: Exception) {
            null
        }
    }

    actual fun getExpirationTimeInSeconds(token: String): Long? {
        return extractClaims(token)?.expirationTime
    }

    actual fun getRemainingValidityInSeconds(token: String): Long? {
        val expirationTime = getExpirationTimeInSeconds(token) ?: return null
        val currentTimeInSeconds = kotlin.time.Clock.System.now().epochSeconds
        val remaining = expirationTime - currentTimeInSeconds
        return if (remaining > 0) remaining else null
    }

    private fun decodeBase64(input: String): String {
        val padded = input + "=".repeat((4 - input.length % 4) % 4)
        val decoded = Base64.UrlSafe.decode(padded)
        return decoded.decodeToString()
    }
}