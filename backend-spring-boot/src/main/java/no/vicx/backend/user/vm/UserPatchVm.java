package no.vicx.backend.user.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import no.vicx.backend.user.validation.AtLeastOneNotNull;
import no.vicx.database.user.VicxUser;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

@AtLeastOneNotNull
@Schema(description = "Represents a partial update payload for a user. At least one field (name or email) " +
        "must be provided to apply the update. Designed for scenarios where only specific user attributes " +
        "need to be updated.")
public record UserPatchVm(

        @Schema(
                description = "The name of the user to update. This field is optional, and only provided if it needs to be changed.",
                example = "John Doe",
                requiredMode = NOT_REQUIRED)
        @Size(min = 4, max = 255)
        String name,

        @Schema(
                description = "The email address of the user to update. This field is optional, and only provided if it needs to be changed.",
                example = "johndoe@example.com",
                requiredMode = NOT_REQUIRED)
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
