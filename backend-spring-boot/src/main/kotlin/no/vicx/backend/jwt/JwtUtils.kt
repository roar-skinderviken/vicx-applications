package no.vicx.backend.jwt

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders


object JwtUtils {
    const val BEARER_PREFIX: String = "Bearer "

    fun detectJwtToken(request: HttpServletRequest): Boolean {
        val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return false
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) return false

        val token = authorizationHeader.substring(BEARER_PREFIX.length) // Remove "Bearer " prefix
        return token.split('.').size == 3
    }
}
