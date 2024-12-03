package no.vicx.backend.user.vm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import no.vicx.backend.user.validation.CurrentPassword;
import no.vicx.database.user.VicxUser;
import org.springframework.security.crypto.password.PasswordEncoder;

public record ChangePasswordVm(
        @NotNull
        @Size(min = EXISTING_PASSWORD_MIN_LENGTH, max = EXISTING_PASSWORD_MAX_LENGTH)
        @CurrentPassword(minLength = EXISTING_PASSWORD_MIN_LENGTH, maxLength = EXISTING_PASSWORD_MAX_LENGTH)
        String currentPassword,

        @NotNull
        @Size(min = 8, max = 255)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "{vicx.constraints.password.Pattern.message}")
        String newPassword) {

    private static final int EXISTING_PASSWORD_MIN_LENGTH = 4;
    private static final int EXISTING_PASSWORD_MAX_LENGTH = 255;

    public VicxUser applyPatch(VicxUser target, PasswordEncoder passwordEncoder) {
        target.setPassword(passwordEncoder.encode(newPassword));
        return target;
    }
}
