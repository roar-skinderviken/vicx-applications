package no.vicx.authserver

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.util.UriComponentsBuilder

object HtmlUnitTestUtils {
    const val REDIRECT_URI = "http://localhost:3000/api/auth/callback/next-app-client"

    fun authorizationRequestUri(scopes: String = "openid"): String =
        UriComponentsBuilder
            .fromPath("/oauth2/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", "next-app-client")
            .queryParam("scope", scopes)
            .queryParam("state", "state")
            .queryParam("redirect_uri", REDIRECT_URI)
            .toUriString()

    fun withMockUser(
        username: String = "user1",
        roles: Set<String> = setOf("ROLE_USER"),
    ) {
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(
                username,
                null,
                roles.map(::SimpleGrantedAuthority),
            )
    }
}
