package no.vicx.user.vm

import io.ktor.server.plugins.requestvalidation.*
import kotlinx.serialization.Serializable
import no.vicx.db.model.UserImage
import no.vicx.db.model.VicxUser
import no.vicx.user.ValidationUtils.emailRegex
import no.vicx.user.ValidationUtils.passwordRegex
import no.vicx.user.ValidationUtils.usernameRegex
import no.vicx.user.ValidationUtils.validateNameLen
import no.vicx.user.ValidationUtils.validatePasswordLen

@Serializable
data class CreateUserVm(
    val username: String = "",
    val password: String = "",
    val name: String = "",
    val email: String = "",
    val recaptchaToken: String = "",
) {
    fun toDbModel(
        encryptedPassword: String,
        userImage: UserImage? = null
    ): VicxUser = VicxUser(
        username = username,
        password = encryptedPassword,
        name = name,
        email = email,
        userImage = userImage
    )

    fun validate(): ValidationResult {
        val validationErrors = mutableListOf<String>()

        // username
        when {
            username.isBlank() -> validationErrors.add("Username cannot be blank")
            username.validateNameLen() -> validationErrors.add("Username must be between 4 and 255 characters")
            !usernameRegex.matches(username) ->
                validationErrors.add("Username can only contain letters, numbers, hyphens, and underscores")
        }

        // password
        when {
            password.isBlank() -> validationErrors.add("Password cannot be blank")
            password.validatePasswordLen() -> validationErrors.add("Password must be between 8 and 255 characters")
            !passwordRegex.matches(password) ->
                validationErrors.add("Password must contain at least one lowercase letter, one uppercase letter, and one digit")
        }

        // name
        when {
            name.isBlank() -> validationErrors.add("Name cannot be blank")
            name.validateNameLen() -> validationErrors.add("Name must be between 4 and 255 characters")
        }

        // email
        when {
            email.isBlank() -> validationErrors.add("Email cannot be blank")
            !emailRegex.matches(email) -> validationErrors.add("Email format is invalid")
        }

        // reCAPTCHA
        if (recaptchaToken.isBlank()) validationErrors.add("recaptchaToken cannot be blank")

        return if (validationErrors.isNotEmpty()) ValidationResult.Invalid(validationErrors)
        else ValidationResult.Valid
    }
}
