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
        var userImage = new UserImage();
        userImage.setImageData(new byte[]{1, 2, 3});
        userImage.setContentType(MediaType.IMAGE_PNG_VALUE);
        return userImage;
    }

    public static VicxUser createUserInTest(UserImage userImage) {
        var user = new VicxUser();
        user.setUsername(EXISTING_USERNAME);
        user.setPassword("~password~");
        user.setName("~name~");
        user.setEmail("~email~");

        if (userImage != null) {
            user.setUserImage(userImage);
        }
        return user;
    }
}
