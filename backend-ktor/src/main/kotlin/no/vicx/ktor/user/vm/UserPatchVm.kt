package no.vicx.ktor.user.vm

import io.ktor.server.plugins.requestvalidation.*
import kotlinx.serialization.Serializable
import no.vicx.ktor.user.ValidationUtils.emailRegex
import no.vicx.ktor.user.ValidationUtils.validateNameLen

@Serializable
data class UserPatchVm(
    val name: String = "",
    val email: String = ""
) {
    private val isEmpty: Boolean get() = name.isBlank() && email.isBlank()

    fun validate(): ValidationResult {
        val validationErrors = mutableListOf<String>()

        // name
        when {
            isEmpty -> validationErrors.add("Name and email cannot both be blank")
            name.isNotBlank() && name.validateNameLen() -> validationErrors.add("Name must be between 4 and 255 characters")
        }

        // email
        when {
            isEmpty -> validationErrors.add("Email and name cannot both be blank")
            email.isNotBlank() && !emailRegex.matches(email) -> validationErrors.add("Email format is invalid")
        }

        return if (validationErrors.isNotEmpty()) ValidationResult.Invalid(validationErrors)
        else ValidationResult.Valid
    }
}
