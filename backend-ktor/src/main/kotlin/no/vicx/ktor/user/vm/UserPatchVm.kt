package no.vicx.ktor.user.vm

import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable
import no.vicx.ktor.user.ValidationUtils.validateEmail
import no.vicx.ktor.user.ValidationUtils.validateName

@Serializable
data class UserPatchVm(
    val name: String = "",
    val email: String = "",
) {
    private val isEmpty: Boolean get() = name.isBlank() && email.isBlank()

    fun validate(): ValidationResult {
        val validationErrors = mutableListOf<String>()

        // name
        when {
            isEmpty -> validationErrors.add("Name and email cannot both be blank")
            name.isNotEmpty() -> name.validateName(validationErrors)
        }

        // email
        when {
            isEmpty -> validationErrors.add("Email and name cannot both be blank")
            email.isNotEmpty() -> email.validateEmail(validationErrors, false)
        }

        return if (validationErrors.isNotEmpty()) {
            ValidationResult.Invalid(validationErrors)
        } else {
            ValidationResult.Valid
        }
    }
}
