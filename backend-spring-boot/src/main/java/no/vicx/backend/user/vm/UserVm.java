package no.vicx.backend.user.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import no.vicx.database.user.VicxUser;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "A view model representing a user, containing essential user details like " +
        "username, name, email, and profile image status.")
public record UserVm(

        @Schema(
                description = "The username of the user.",
                example = "johndoe",
                requiredMode = REQUIRED)
        String username,

        @Schema(
                description = "The name of the user.",
                example = "John Doe",
                requiredMode = REQUIRED)
        String name,

        @Schema(
                description = "The email address of the user.",
                example = "johndoe@example.com",
                requiredMode = REQUIRED)
        String email,

        @Schema(
                description = "Indicates whether the user has a profile image.",
                example = "true",
                requiredMode = REQUIRED)
        boolean hasImage
) {
    public static UserVm fromVicxUser(VicxUser user) {
        return new UserVm(
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getUserImage() != null
        );
    }
}
