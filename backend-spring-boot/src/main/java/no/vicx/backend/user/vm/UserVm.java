package no.vicx.backend.user.vm;

import no.vicx.database.user.VicxUser;

public record UserVm(
        String username,
        String name,
        String email,
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
