package no.vicx.backend.user;

import no.vicx.backend.user.vm.UserVm;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class UserTestUtils {

    private UserTestUtils() {
    }

    public static MockMultipartFile createMultipartFile(
            String fileName, String fileContentType) throws IOException {
        var imageResource = new ClassPathResource(fileName);
        return new MockMultipartFile(
                "image",
                fileName,
                fileContentType,
                imageResource.getInputStream()
        );
    }

    static final UserVm VALID_USER_VM = new UserVm(
            "user1",
            "P4ssword",
            "The User",
            "user@example.com",
            "mock-token");

    static void assertUserVm(VicxUser user, UserVm userVm) {
        assertNotNull(userVm);
        assertNull(userVm.password());
        assertEquals(user.getUsername(), userVm.username());
        assertEquals(user.getEmail(), userVm.email());
        assertEquals(user.getName(), userVm.name());
    }

    static Stream<Arguments> mockMultipartFileProvider() throws IOException {
        return Stream.of(
                Arguments.of(createMultipartFile("profile.png", MediaType.IMAGE_PNG_VALUE), true),
                Arguments.of(
                        new MockMultipartFile(
                                "empty-file.png",
                                "empty.png",
                                MediaType.IMAGE_PNG_VALUE,
                                new byte[0]
                        ), true),
                Arguments.of(null, false)
        );
    }

    static Stream<Arguments> invalidImageProvider() throws IOException {
        return Stream.of(
                Arguments.of(
                        createMultipartFile("test-gif.gif", MediaType.IMAGE_GIF_VALUE),
                        "image", "Only PNG and JPG files are allowed"
                ),
                Arguments.of(
                        createMultipartFile("too-large.png", MediaType.IMAGE_PNG_VALUE),
                        "image", "File size exceeds the maximum allowed size of 51200 bytes"
                )
        );
    }

    static Stream<Arguments> invalidUserVmProvider() {
        return Stream.of(
                Arguments.of(
                        new UserVm(null, "P4ssword", "The User", "user@example.com", "mock-token"),
                        "username", "Username cannot be null"
                ),
                Arguments.of(
                        new UserVm(" ".repeat(4), "P4ssword", "The User", "user@example.com", "mock-token"),
                        "username", "Username can only contain letters, numbers, hyphens, and underscores"
                ),
                Arguments.of(
                        new UserVm("a", "P4ssword", "The User", "user@example.com", "mock-token"),
                        "username", "It must have minimum 4 and maximum 255 characters"
                ),
                Arguments.of(
                        new UserVm("a".repeat(256), "P4ssword", "The User", "user@example.com", "mock-token"),
                        "username", "It must have minimum 4 and maximum 255 characters"
                ),
                Arguments.of(
                        new UserVm("John Doe", "P4ssword", "The User", "user@example.com", "mock-token"),
                        "username", "Username can only contain letters, numbers, hyphens, and underscores"
                ),
                Arguments.of(
                        new UserVm("John:Doe", "P4ssword", "The User", "user@example.com", "mock-token"),
                        "username", "Username can only contain letters, numbers, hyphens, and underscores"
                ),

                Arguments.of(
                        new UserVm("user1", null, "The User", "user@example.com", "mock-token"),
                        "password", "Cannot be null"
                ),
                Arguments.of(
                        new UserVm("user1", "Aa1Aa1", "The User", "user@example.com", "mock-token"),
                        "password", "It must have minimum 8 and maximum 255 characters"
                ),
                Arguments.of(
                        new UserVm("user1", "Aa1".repeat(90), "The User", "user@example.com", "mock-token"),
                        "password", "It must have minimum 8 and maximum 255 characters"
                ),
                Arguments.of(
                        new UserVm("user1", "a".repeat(8), "The User", "user@example.com", "mock-token"),
                        "password", "Password must have at least one uppercase, one lowercase letter and one number"
                ),

                Arguments.of(
                        new UserVm("user1", "P4ssword", "The User", null, "mock-token"),
                        "email", "Cannot be null"
                ),
                Arguments.of(
                        new UserVm("user1", "P4ssword", "The User", "a", "mock-token"),
                        "email", "It must be a well-formed email address"
                ),

                Arguments.of(
                        new UserVm("user1", "P4ssword", null, "user@example.com", "mock-token"),
                        "name", "Cannot be null"
                ),
                Arguments.of(
                        new UserVm("user1", "P4ssword", "a".repeat(3), "user@example.com", "mock-token"),
                        "name", "It must have minimum 4 and maximum 255 characters"
                ),
                Arguments.of(
                        new UserVm("user1", "P4ssword", "a".repeat(256), "user@example.com", "mock-token"),
                        "name", "It must have minimum 4 and maximum 255 characters"
                ),

                Arguments.of(
                        new UserVm("user1", "P4ssword", "The User", "user@example.com", null),
                        "recaptchaToken", "reCAPTCHA cannot be null or blank"
                ),
                Arguments.of(
                        new UserVm("user1", "P4ssword", "The User", "user@example.com", ""),
                        "recaptchaToken", "reCAPTCHA cannot be null or blank"
                ),
                Arguments.of(
                        new UserVm("user1", "P4ssword", "The User", "user@example.com", "  "),
                        "recaptchaToken", "reCAPTCHA cannot be null or blank"
                )
        );
    }
}
