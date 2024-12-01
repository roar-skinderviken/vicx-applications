package no.vicx.backend.user.vm;

import jakarta.validation.constraints.*;
import no.vicx.backend.user.validation.RecaptchaThenUniqueUsername;
import no.vicx.database.user.VicxUser;
import org.springframework.security.crypto.password.PasswordEncoder;

@RecaptchaThenUniqueUsername
public record UserVm(
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

    public VicxUser toNewVicxUser(PasswordEncoder passwordEncoder) {
        return new VicxUser(username, passwordEncoder.encode(password), name, email, null);
    }

    public static UserVm fromVicxUser(VicxUser user) {
        return new UserVm(
                user.getUsername(),
                null,
                user.getName(),
                user.getEmail(),
                null
        );
    }
}
