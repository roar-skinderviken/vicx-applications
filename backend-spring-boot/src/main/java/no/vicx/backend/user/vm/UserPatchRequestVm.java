package no.vicx.backend.user.vm;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import no.vicx.backend.user.validation.AtLeastOneNotNull;
import no.vicx.database.user.VicxUser;
import org.springframework.security.crypto.password.PasswordEncoder;

@AtLeastOneNotNull
public record UserPatchRequestVm(
        @Size(min = 8, max = 255)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "{vicx.constraints.password.Pattern.message}")
        String password,

        @Size(min = 4, max = 255)
        String name,

        @Email
        String email) {

    public Boolean isEmpty() {
        return password == null && name == null && email == null;
    }

    public VicxUser applyPatch(VicxUser target, PasswordEncoder passwordEncoder) {
        if (password != null) {
            target.setPassword(passwordEncoder.encode(password));
        }
        if (name != null) {
            target.setName(name);
        }
        if (email != null) {
            target.setEmail(email);
        }
        return target;
    }
}
