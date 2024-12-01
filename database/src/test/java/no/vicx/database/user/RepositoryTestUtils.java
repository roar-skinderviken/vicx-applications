package no.vicx.database.user;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static no.vicx.database.user.VicxUser.VALID_BCRYPT_PASSWORD;

@UtilityClass
public class RepositoryTestUtils {

    static VicxUser createValidUser() {
        return createValidUser(null);
    }

    static VicxUser createValidUser(UserImage userImage) {
        return new VicxUser(
                "user1", VALID_BCRYPT_PASSWORD, "Foo Bar", "user1@vicx.no", userImage);
    }

    static final String IMAGE_PNG = "image/png";
    static final String IMAGE_JPEG = "image/jpeg";

    static UserImage createPngUserImage() throws IOException {
        return createUserImage("test-png.png", IMAGE_PNG);
    }

    static UserImage createJpegUserImage() throws IOException {
        return createUserImage("test-jpg.jpg", IMAGE_JPEG);
    }

    static UserImage createUserImage(String resourceName, String mimeType) throws IOException {
        return new UserImage(
                new ClassPathResource(resourceName).getContentAsByteArray(),
                mimeType);
    }
}
