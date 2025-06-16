package no.vicx.backend

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

object SecurityTestUtils {

    private const val BEARER_PREFIX = "Bearer "
    const val VICX_USER_TOKEN = "~vicx-user-token~"
    const val GITHUB_USER_TOKEN = "~github-user-token~"

    const val AUTH_HEADER_IN_TEST = BEARER_PREFIX + VICX_USER_TOKEN
    const val AUTH_HEADER_IN_TEST_GITHUB = BEARER_PREFIX + GITHUB_USER_TOKEN

    private const val VALID_JWT_STRING = "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9" +
            ".eyJzdWIiOiAiMTIzNDU2Nzg5MCIsICJuYW1lIjogIkpvaG4gRG9lIiwgImlhdCI6IDE1MTYyMzkwMjJ9" +
            ".aGVsbG9fc2lnbmF0dXJlX2Jhc2U2NA=="

    fun createPrincipalInTest(roles: List<String>): OAuth2AuthenticatedPrincipal {
        return object : OAuth2AuthenticatedPrincipal {
            override fun getAttributes(): Map<String, Any> {
                return emptyMap()
            }

            override fun getAuthorities(): List<SimpleGrantedAuthority> = roles
                .map(::SimpleGrantedAuthority)// { role -> SimpleGrantedAuthority(role) }

            override fun getName(): String = "user1"
        }
    }

    fun createJwtInTest(roles: List<String?>?): Jwt {
        return Jwt
            .withTokenValue(VALID_JWT_STRING)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(30))
            .header("alg", "none")
            .claim("sub", "user1")
            .claim("roles", roles)
            .build()
    }
}