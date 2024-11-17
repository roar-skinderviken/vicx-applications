package no.vicx.backend.user;

import no.vicx.backend.user.vm.UserVm;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.core.io.ClassPathResource;
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

    static UserVm createValidUserVm() {
        return new UserVm(
                "user1",
                "P4ssword",
                "user@example.com",
                "The User");
    }

    static UserVm createUserVm(
            String username, String password, String email, String name) {
        return new UserVm(username, password, email, name);
    }

    static VicxUser createValidUser() {
        return createValidUserVm().toNewVicxUser();
    }

    static void assertUserVm(VicxUser user, UserVm userVm) {
        assertNotNull(userVm);
        assertNull(userVm.password());
        assertEquals(user.getUsername(), userVm.username());
        assertEquals(user.getEmail(), userVm.email());
        assertEquals(user.getName(), userVm.name());
    }

    static Stream<Arguments> mockMultipartFileProvider() throws IOException {
        return Stream.of(
                Arguments.of(createMultipartFile("profile.png", "image/png"), true),
                Arguments.of(
                        new MockMultipartFile(
                                "empty-file.png",
                                "empty.png",
                                "image/png",
                                new byte[0]
                        ), true),
                Arguments.of(null, false)
        );
    }

    static Stream<Arguments> invalidImageProvider() throws IOException {
        return Stream.of(
                Arguments.of(
                        createMultipartFile("test-gif.gif", "image/gif"),
                        "image", "Only PNG and JPG files are allowed"
                ),
                Arguments.of(
                        createMultipartFile("too-large.png", "image/png"),
                        "image", "File size exceeds the maximum allowed size of 51200 bytes"
                )
        );
    }

    static Stream<Arguments> invalidUserVmProvider() {
        return Stream.of(
                Arguments.of(
                        createUserVm(null, "P4ssword", "user@example.com", "The User"),
                        "username", "Username cannot be null"
                ),
                Arguments.of(
                        createUserVm("a", "P4ssword", "user@example.com", "The User"),
                        "username", "It must have minimum 4 and maximum 255 characters"
                ),
                Arguments.of(
                        createUserVm("a".repeat(256), "P4ssword", "user@example.com", "The User"),
                        "username", "It must have minimum 4 and maximum 255 characters"
                ),

                Arguments.of(
                        createUserVm("user1", null, "user@example.com", "The User"),
                        "password", "Cannot be null"
                ),
                Arguments.of(
                        createUserVm("user1", "Aa1Aa1", "user@example.com", "The User"),
                        "password", "It must have minimum 8 and maximum 255 characters"
                ),
                Arguments.of(
                        createUserVm("user1", "Aa1".repeat(90), "user@example.com", "The User"),
                        "password", "It must have minimum 8 and maximum 255 characters"
                ),
                Arguments.of(
                        createUserVm("user1", "a".repeat(8), "user@example.com", "The User"),
                        "password", "Password must have at least one uppercase, one lowercase letter and one number"
                ),

                Arguments.of(
                        createUserVm("user1", "P4ssword", null, "The User"),
                        "email", "Cannot be null"
                ),
                Arguments.of(
                        createUserVm("user1", "P4ssword", "a", "The User"),
                        "email", "It must be a well-formed email address"
                ),

                Arguments.of(
                        createUserVm("user1", "P4ssword", "user@example.com", null),
                        "name", "Cannot be null"
                ),
                Arguments.of(
                        createUserVm("user1", "P4ssword", "user@example.com", "a".repeat(3)),
                        "name", "It must have minimum 4 and maximum 255 characters"
                ),
                Arguments.of(
                        createUserVm("user1", "P4ssword", "user@example.com", "a".repeat(256)),
                        "name", "It must have minimum 4 and maximum 255 characters"
                )
        );
    }
}
