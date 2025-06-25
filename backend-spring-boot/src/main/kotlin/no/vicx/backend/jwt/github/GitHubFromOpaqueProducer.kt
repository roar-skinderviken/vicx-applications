package no.vicx.backend.jwt.github

import org.springframework.cache.annotation.Cacheable
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException

@Service
class GitHubFromOpaqueProducer(
    private val userFetcher: GitHubUserFetcher,
) {
    @Cacheable(value = ["GITHUB_OPAQUE_PRINCIPALS"])
    fun createPrincipal(token: String): OAuth2AuthenticatedPrincipal =
        runCatching {
            userFetcher
                .fetchUser(token)
                .toPrincipal()
        }.getOrElse { thrown ->
            val message =
                if (thrown is HttpClientErrorException) {
                    "Invalid or expired GitHub access token"
                } else {
                    "Error validating GitHub token"
                }

            throw BadOpaqueTokenException(message, thrown)
        }
}
