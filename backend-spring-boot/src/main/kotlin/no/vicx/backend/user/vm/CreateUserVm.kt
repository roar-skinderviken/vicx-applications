package no.vicx.backend.user.vm

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import no.vicx.backend.user.validation.RecaptchaThenUniqueUsername

@RecaptchaThenUniqueUsername
@Schema(
    description =
        "Represents the structure for creating a new user, including fields for " +
            "username, password, name, email, and reCAPTCHA validation. All fields are subject to " +
            "validation to ensure proper formatting and prevent bot submissions.",
)
data class CreateUserVm(
    @Schema(
        description = "Username of the user, must be alphanumeric and may contain underscores or hyphens.",
        example = "john_doe123",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:Size(min = 4, max = 255)
    @field:Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "{vicx.constraints.username.Pattern.message}")
    val username: String = "",
    @Schema(
        description = "Password of the user. It must contain at least one lowercase letter, one uppercase letter, and one digit.",
        example = "Password123",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:Size(min = 8, max = 255)
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "{vicx.constraints.password.Pattern.message}",
    )
    val password: String = "",
    @Schema(
        description = "Full name of the user.",
        example = "John Doe",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:Size(min = 4, max = 255)
    val name: String = "",
    @Schema(
        description = "Email address of the user. Must be a valid email format.",
        example = "john.doe@example.com",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:Email
    val email: String = "",
    @Schema(
        description = "reCAPTCHA token for validation to prevent bot submissions.",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:NotBlank(message = "{vicx.constraints.reCAPTCHA.NotBlank.message}")
    val recaptchaToken: String = "",
)
