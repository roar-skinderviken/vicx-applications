package no.vicx.backend.user.vm;

import jakarta.validation.constraints.*;
import no.vicx.backend.user.validation.RecaptchaThenUniqueUsername;
import no.vicx.database.user.VicxUser;

@RecaptchaThenUniqueUsername(
        recaptchaMessage = "{vicx.constraints.reCAPTCHA.message}",
        usernameMinLength = 4,
        uniqueUsernameMessage = "{vicx.constraints.username.UniqueUsername.message}"
)
public record UserVm(
        @NotBlank(message = "{vicx.constraints.username.NotBlank.message}")
        @Size(min = 4, max = 255)
        String username,

        @NotNull
        @Size(min = 8, max = 255)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "{vicx.constraints.Pattern.message}")
        String password,

        @NotNull
        @Email
        @NotNull String email,

        @NotNull
        @Size(min = 4, max = 255)
        String name,

        @NotBlank(message = "{vicx.constraints.reCAPTCHA.NotBlank.message}")
        String recaptchaToken
) {
    public VicxUser toNewVicxUser() {
        var user = new VicxUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    public static UserVm fromVicxUser(VicxUser user) {
        return new UserVm(
                user.getUsername(),
                null,
                user.getEmail(),
                user.getName(),
                null
        );
    }
}
