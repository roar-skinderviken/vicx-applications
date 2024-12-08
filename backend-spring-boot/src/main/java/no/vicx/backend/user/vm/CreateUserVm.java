package no.vicx.backend.user.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import no.vicx.backend.user.validation.RecaptchaThenUniqueUsername;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@RecaptchaThenUniqueUsername
@Schema(description = "Represents the structure for creating a new user, including fields for " +
        "username, password, name, email, and reCAPTCHA validation. All fields are subject to " +
        "validation to ensure proper formatting and prevent bot submissions.")
public record CreateUserVm(

        @Schema(
                description = "Username of the user, must be alphanumeric and may contain underscores or hyphens.",
                example = "john_doe123",
                requiredMode = REQUIRED)
        @NotNull(message = "{vicx.constraints.username.NotNull.message}")
        @Size(min = 4, max = 255)
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "{vicx.constraints.username.Pattern.message}")
        String username,

        @Schema(
                description = "Password of the user. It must contain at least one lowercase letter, one uppercase letter, and one digit.",
                example = "Password123",
                requiredMode = REQUIRED)
        @NotNull
        @Size(min = 8, max = 255)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "{vicx.constraints.password.Pattern.message}")
        String password,

        @Schema(
                description = "Full name of the user.",
                example = "John Doe",
                requiredMode = REQUIRED)
        @NotNull
        @Size(min = 4, max = 255)
        String name,

        @Schema(
                description = "Email address of the user. Must be a valid email format.",
                example = "john.doe@example.com",
                requiredMode = REQUIRED)
        @NotNull
        @Email
        String email,

        @Schema(
                description = "reCAPTCHA token for validation to prevent bot submissions.",
                requiredMode = REQUIRED)
        @NotBlank(message = "{vicx.constraints.reCAPTCHA.NotBlank.message}")
        String recaptchaToken) {
}
