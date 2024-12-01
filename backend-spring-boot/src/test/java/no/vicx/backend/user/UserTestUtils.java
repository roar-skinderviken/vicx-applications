package no.vicx.backend.user;

import no.vicx.backend.user.vm.UserVm;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.stream.Stream;

public final class UserTestUtils {

    private UserTestUtils() {
    }

    public static VicxUser createValidVicxUser() {
        return new VicxUser("user1", "P4ssword",
                "The User", "user@example.com", null);
    }

    public static final UserVm VALID_USER_VM = new UserVm(
            "user1",
            "P4ssword",
            "The User",
            "user@example.com",
            "mock-token");

    public static Stream<Arguments> mockMultipartFileProvider() throws IOException {
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

    static MockMultipartFile createMultipartFile(
            String fileName, String fileContentType) throws IOException {
        return new MockMultipartFile(
                "image",
                fileName,
                fileContentType,
                new ClassPathResource(fileName).getInputStream()
        );
    }
}
