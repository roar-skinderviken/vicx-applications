package no.vicx.backend.user.vm

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import no.vicx.backend.user.validation.CurrentPassword
import no.vicx.database.user.VicxUser
import org.springframework.security.crypto.password.PasswordEncoder


@Schema(
    description = "Represents the structure for changing a user's password. " +
            "This includes validation for the current password and strength requirements for the new password."
)
data class ChangePasswordVm(
    @field:CurrentPassword(
        minLength = EXISTING_PASSWORD_MIN_LENGTH,
        maxLength = EXISTING_PASSWORD_MAX_LENGTH
    )
    @field:NotNull
    @field:Size(
        min = EXISTING_PASSWORD_MIN_LENGTH,
        max = EXISTING_PASSWORD_MAX_LENGTH
    )
    val currentPassword: String? = null,

    @Schema(
        description = "The new password for the user, which must meet the specified strength requirements.",
        example = "NewPassword456",
        requiredMode = RequiredMode.REQUIRED
    )
    @field:NotNull
    @field:Size(min = 8, max = 255)
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "{vicx.constraints.password.Pattern.message}"
    )
    val password: String? = null
) {
    fun applyPatch(
        target: VicxUser,
        passwordEncoder: PasswordEncoder
    ): VicxUser = target.also { user ->
        user.password = passwordEncoder.encode(password)
    }

    companion object {
        private const val EXISTING_PASSWORD_MIN_LENGTH = 4
        private const val EXISTING_PASSWORD_MAX_LENGTH = 255
    }
}
