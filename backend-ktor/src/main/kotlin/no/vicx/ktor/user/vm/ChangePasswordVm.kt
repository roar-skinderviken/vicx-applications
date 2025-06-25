package no.vicx.ktor.user.vm

import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable
import no.vicx.ktor.user.ValidationUtils.validatePassword

@Serializable
data class ChangePasswordVm(
    val currentPassword: String = "",
    val password: String = "",
) {
    fun validate(): ValidationResult {
        val validationErrors = mutableListOf<String>()

        when {
            currentPassword.isBlank() -> validationErrors.add("currentPassword cannot be blank")
            currentPassword.length !in 4..255 -> validationErrors.add("currentPassword must be between 4 and 255 characters")
        }

        password.validatePassword(validationErrors)

        return if (validationErrors.isNotEmpty()) {
            ValidationResult.Invalid(validationErrors)
        } else {
            ValidationResult.Valid
        }
    }
}
