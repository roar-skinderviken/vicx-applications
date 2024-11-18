package no.vicx.database.user;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public final class RepositoryTestUtils {

    private RepositoryTestUtils() {
    }

    static VicxUser createValidUser() {
        var user = new VicxUser();
        user.setUsername("user1");
        user.setName("Foo Bar");
        user.setEmail("user1@vicx.no");
        user.setPassword("password1");
        return user;
    }

    static UserImage createUserImage() throws IOException {
        var userImage = new UserImage();
        userImage.setContentType("image/png");

        var imageResource = new ClassPathResource("profile.png");
        userImage.setImageData(imageResource.getContentAsByteArray());

        return userImage;
    }
}
