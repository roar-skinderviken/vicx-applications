package no.vicx.backend.user.vm;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import no.vicx.backend.user.validation.AtLeastOneNotNull;
import no.vicx.database.user.VicxUser;

@AtLeastOneNotNull
public record UserPatchVm(
        @Size(min = 4, max = 255)
        String name,

        @Email
        String email) {

    public Boolean isEmpty() {
        return name == null && email == null;
    }

    public VicxUser applyPatch(VicxUser target) {
        if (name != null) {
            target.setName(name);
        }
        if (email != null) {
            target.setEmail(email);
        }
        return target;
    }
}
