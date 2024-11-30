package no.vicx.authserver;

import no.vicx.database.user.UserImage;
import no.vicx.database.user.VicxUser;
import org.springframework.http.MediaType;

public final class UserTestUtils {
    private UserTestUtils() {
    }

    public static final String DEFAULT_USERNAME_IN_TEST = "user1";
    public static final String EXISTING_USERNAME = "~username~";
    public static final String NON_EXISTING_USERNAME = "user2";

    public static UserImage createUserImageInTest() {
        return new UserImage(new byte[]{1, 2, 3}, MediaType.IMAGE_PNG_VALUE);
    }

    public static VicxUser createUserInTest(UserImage userImage) {
        return new VicxUser(
                EXISTING_USERNAME, "~password~", "~name~", "~email~", userImage);
    }
}
