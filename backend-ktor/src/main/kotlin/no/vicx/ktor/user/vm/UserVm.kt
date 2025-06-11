package no.vicx.ktor.user.vm

import kotlinx.serialization.Serializable

@Serializable
data class UserVm(
    val username: String,
    val name: String,
    val email: String,
    val hasImage: Boolean
)