package no.vicx.ktor.user.vm

import io.ktor.server.plugins.requestvalidation.*
import kotlinx.serialization.Serializable
import no.vicx.ktor.db.model.UserImage
import no.vicx.ktor.db.model.VicxUser
import no.vicx.ktor.user.ValidationUtils.validateEmail
import no.vicx.ktor.user.ValidationUtils.validateName
import no.vicx.ktor.user.ValidationUtils.validatePassword
import no.vicx.ktor.user.ValidationUtils.validateUsername

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

        username.validateUsername(validationErrors)
        password.validatePassword(validationErrors)
        name.validateName(validationErrors)
        email.validateEmail(validationErrors)

        // reCAPTCHA
        if (recaptchaToken.isBlank()) validationErrors.add("recaptchaToken cannot be blank")

        return if (validationErrors.isNotEmpty()) ValidationResult.Invalid(validationErrors)
        else ValidationResult.Valid
    }
}
