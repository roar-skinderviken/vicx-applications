package no.vicx.backend.user.vm;

import jakarta.validation.constraints.*;
import no.vicx.backend.user.validation.RecaptchaThenUniqueUsername;

@RecaptchaThenUniqueUsername
public record CreateUserVm(
        @NotNull(message = "{vicx.constraints.username.NotNull.message}")
        @Size(min = 4, max = 255)
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "{vicx.constraints.username.Pattern.message}")
        String username,

        @NotNull
        @Size(min = 8, max = 255)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "{vicx.constraints.password.Pattern.message}")
        String password,

        @NotNull
        @Size(min = 4, max = 255)
        String name,

        @NotNull
        @Email
        String email,

        @NotBlank(message = "{vicx.constraints.reCAPTCHA.NotBlank.message}")
        String recaptchaToken) {
}
