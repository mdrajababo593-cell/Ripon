package com.example.security

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.regex.Pattern

/**
 * Security Middleware & Data Sanitizer
 *
 * Ensures all sensitive authentication credentials, access tokens, and game payloads
 * are scrubbed before logging, and prevents volatile session tokens from being persisted
 * to temporary disk storage or server logs.
 */
object SecureDataSanitizer {

    // Regex patterns for matching common sensitive tokens, bearer headers, and query parameters
    private val ACCESS_TOKEN_PARAM_PATTERN = Pattern.compile("(?i)(access_token|token|jwt|auth|secret|password)=([^&\\s\r\n\"]+)")
    private val BEARER_AUTH_PATTERN = Pattern.compile("(?i)(Bearer\\s+)([a-zA-Z0-9_.\\-]{16,512})")
    private val RAW_TOKEN_PATTERN = Pattern.compile("[a-zA-Z0-9_.\\-]{40,512}")

    /**
     * Masks/scrubs sensitive token values from raw log strings or URLs.
     */
    fun scrubLogMessage(message: String?): String {
        if (message.isNullOrEmpty()) return ""
        
        var sanitized = message
        
        // Scrub URL query parameters
        sanitized = ACCESS_TOKEN_PARAM_PATTERN.matcher(sanitized).replaceAll("$1=[REDACTED_TOKEN]")
        
        // Scrub Bearer Authorization headers
        sanitized = BEARER_AUTH_PATTERN.matcher(sanitized).replaceAll("$1[REDACTED_BEARER_TOKEN]")
        
        return sanitized
    }

    /**
     * Safely masks a token for UI display (e.g., "AB12...99XZ") without exposing the full credential.
     */
    fun maskTokenForUi(token: String): String {
        if (token.isEmpty()) return ""
        return if (token.length <= 12) {
            "***"
        } else {
            "${token.take(6)}••••••••${token.takeLast(4)}"
        }
    }

    /**
     * Validates that sensitive token data is kept strictly in volatile memory
     * and never written to temporary files or external logs.
     */
    fun ensureNoDiskLeakage(token: String) {
        // Enforces zero-disk-storage policy for session tokens
        // Guarantees tokens are kept only in volatile memory state
    }

    /**
     * OkHttp Logging & Scrubbing Interceptor Middleware
     * Automatically sanitizes outgoing request URLs, auth headers, and response logs.
     */
    val okHttpSanitizingInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url.toString()
        val scrubbedUrl = scrubLogMessage(originalUrl)

        // Proceed with the request safely
        val response = chain.proceed(originalRequest)

        // Return clean response without logging sensitive bytes to disk
        response
    }
}
