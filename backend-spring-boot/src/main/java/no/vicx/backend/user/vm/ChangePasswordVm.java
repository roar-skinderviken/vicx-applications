package no.vicx.backend.user.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import no.vicx.backend.user.validation.CurrentPassword;
import no.vicx.database.user.VicxUser;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Represents the structure for changing a user's password. " +
        "This includes validation for the current password and strength requirements for the new password.")
public record ChangePasswordVm(

        @Schema(
                description = "The current password of the user, which will be validated to ensure it's correct.",
                example = "OldPassword123",
                requiredMode = REQUIRED)
        @NotNull
        @Size(min = EXISTING_PASSWORD_MIN_LENGTH, max = EXISTING_PASSWORD_MAX_LENGTH)
        @CurrentPassword(minLength = EXISTING_PASSWORD_MIN_LENGTH, maxLength = EXISTING_PASSWORD_MAX_LENGTH)
        String currentPassword,

        @Schema(
                description = "The new password for the user, which must meet the specified strength requirements.",
                example = "NewPassword456",
                requiredMode = REQUIRED)
        @NotNull
        @Size(min = 8, max = 255)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "{vicx.constraints.password.Pattern.message}")
        String password) {

    private static final int EXISTING_PASSWORD_MIN_LENGTH = 4;
    private static final int EXISTING_PASSWORD_MAX_LENGTH = 255;

    public VicxUser applyPatch(VicxUser target, PasswordEncoder passwordEncoder) {
        target.setPassword(passwordEncoder.encode(password));
        return target;
    }
}
