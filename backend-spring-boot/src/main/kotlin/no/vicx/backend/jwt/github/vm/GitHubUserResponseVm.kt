package no.vicx.backend.jwt.github.vm

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal

data class GitHubUserResponseVm(
    val user: GitHubUserVm,
    val grantedScopes: String,
    val token: String
) {
    fun toPrincipal() = DefaultOAuth2AuthenticatedPrincipal(
        user.login,
        mapOf("sub" to user.login),
        listOf(SimpleGrantedAuthority("ROLE_GITHUB_USER"))
    )
}
