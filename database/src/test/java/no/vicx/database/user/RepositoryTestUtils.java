package no.vicx.database.user;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public final class RepositoryTestUtils {

    private RepositoryTestUtils() {
    }

    static VicxUser createValidUser() {
        return new VicxUser(
                "user1", "password1", "Foo Bar", "user1@vicx.no", null);
    }

    static UserImage createUserImage() throws IOException {
        return new UserImage(
                new ClassPathResource("profile.png").getContentAsByteArray(),
                "image/png");
    }
}
