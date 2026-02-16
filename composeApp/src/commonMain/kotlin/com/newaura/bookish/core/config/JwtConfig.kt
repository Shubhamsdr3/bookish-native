package com.newaura.bookish.core.config

/**
 * Configuration for JWT token validation
 */
object JwtConfig {

    /**
     * Backend public key for JWT signature verification
     *
     * To get this key:
     * 1. Ask your backend team for the RSA public key
     * 2. It should be in PEM format starting with "-----BEGIN PUBLIC KEY-----"
     * 3. Replace the placeholder below with your actual public key
     */
    const val BACKEND_PUBLIC_KEY = """
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...
(paste your actual public key here)
-----END PUBLIC KEY-----
    """
}

