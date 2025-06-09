package no.vicx.ktor.user.vm

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordVm(
    val currentPassword: String,
    val password: String
)