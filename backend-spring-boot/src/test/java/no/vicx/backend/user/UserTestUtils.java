package no.vicx.backend.user;

import no.vicx.backend.user.vm.UserVm;
import no.vicx.database.user.VicxUser;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTestUtils {

    private UserTestUtils() {}

    public static String readFileToBase64(String fileName) throws IOException {
        var imageResource = new ClassPathResource(fileName);
        return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(imageResource.getFile()));
    }

    static final String VALID_BASE64_IMAGE;
    static final VicxUser VALID_VICX_USER = createValidVicxUser();
    static final UserVm VALID_USER_VM = createUserVm(
            "user1", "P4ssword", "user@example.com", "The User");

    static UserVm createUserVm(String base64Image) {
        return new UserVm(
                "user1",
                "P4ssword",
                "user@example.com",
                "The User",
                base64Image);
    }

    static {
        try {
            VALID_BASE64_IMAGE = readFileToBase64("profile.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static UserVm createUserVm(
            String username, String password, String email, String name) {
        return new UserVm(username, password, email, name, VALID_BASE64_IMAGE);
    }

    static VicxUser createValidVicxUser() {
        return createUserVm(VALID_BASE64_IMAGE).toNewVicxUser();
    }

    static void assertUserVm(VicxUser user, UserVm userVm) {
        assertNotNull(userVm);
        assertNull(userVm.password());
        assertEquals(user.getUsername(), userVm.username());
        assertEquals(user.getEmail(), userVm.email());
        assertEquals(user.getName(), userVm.name());
        assertEquals(user.getImage(), userVm.image());
    }

    static Stream<Arguments> invalidUserVmProvider() throws IOException {
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
                ),

                Arguments.of(
                        new UserVm("user1", "P4ssword", "user@example.com", "The User", "a"),
                        "image", "Invalid base64 for profile image"
                ),
                Arguments.of(
                        createUserVm(readFileToBase64("test-gif.gif")),
                        "image", "Only PNG and JPG files are allowed"
                ),
                Arguments.of(
                        createUserVm(readFileToBase64("too-large.png")),
                        "image", "File size exceeds the maximum allowed size of 51200 bytes"
                )
        );
    }
}
