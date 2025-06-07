package no.vicx.user.vm

import kotlinx.serialization.Serializable

@Serializable
data class UserPatchVm(
    val name: String,
    val email: String
)
