package no.vicx.backend.jwt.github

import no.vicx.backend.jwt.JwtUtils.BEARER_PREFIX
import no.vicx.backend.jwt.github.vm.GitHubUserResponseVm
import no.vicx.backend.jwt.github.vm.GitHubUserVm
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient


@Service
class GitHubUserFetcher(
    restClientBuilder: RestClient.Builder
) {
    private val restClient = restClientBuilder
        .baseUrl(GITHUB_USER_URL)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun fetchUser(token: String): GitHubUserResponseVm {
        val responseEntity = runCatching {
            restClient
                .get()
                .header(HttpHeaders.AUTHORIZATION, "${BEARER_PREFIX}$token")
                .retrieve()
                .toEntity(GitHubUserVm::class.java)
        }.getOrElse { thrown ->
            if (thrown is HttpClientErrorException) throw thrown
            throw IllegalStateException("Failed to fetch user: ${thrown.message}", thrown)
        }

        val gitHubUserVm = checkNotNull(responseEntity.body) { "User is null" }
        check(!gitHubUserVm.isEmpty) { "User is empty" }

        val scopes = checkNotNull(
            responseEntity.headers.getFirst(SCOPES_HEADER)
        ) { "No scopes header found" }

        return GitHubUserResponseVm(
            user = gitHubUserVm,
            grantedScopes = scopes,
            token = token
        )
    }

    companion object {
        const val GITHUB_USER_URL = "https://api.github.com/user"
        const val SCOPES_HEADER = "X-OAuth-Scopes"
    }
}
