package no.vicx.backend.jwt.github.vm

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubUserVm(
    val id: String = "",
    val login: String = "",
    val name: String = "",
    val email: String = "",
    @JsonProperty("avatar_url") val avatarUrl: String = "" // TODO: Check if this should be init with null
) {
    val isEmpty: Boolean
        get() = id.isBlank()
                && login.isBlank()
                && name.isBlank()
                && email.isBlank()
                && avatarUrl.isBlank()
}
