package no.vicx.backend.jwt.github

import no.vicx.backend.jwt.github.vm.GitHubUserVm

object GitHubTestUtils {
    val githubUserInTest =
        GitHubUserVm(
            "12345",
            "john-doe",
            "John Doe",
            "john.doe@example.com",
            "https://example.com/avatar.jpg",
        )
}
