package no.vicx.backend.user.vm;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import no.vicx.backend.user.validation.ProfileImage;
import no.vicx.backend.user.validation.UniqueUsername;
import no.vicx.database.user.VicxUser;

public record UserVm(
        @NotNull(message = "{vicx.constraints.username.NotNull.message}")
        @Size(min = 4, max = 255)
        @UniqueUsername(message = "{vicx.constraints.username.UniqueUsername.message}")
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

        @ProfileImage
        String image
) {
    public VicxUser toNewVicxUser() {
        var user = new VicxUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setName(name);
        user.setImage(image);

        return user;
    }

    public static UserVm fromVicxUser(VicxUser user) {
        return new UserVm(
                user.getUsername(),
                null,
                user.getEmail(),
                user.getName(),
                user.getImage());
    }
}
