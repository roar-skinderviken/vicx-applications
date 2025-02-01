package no.vicx.backend.user.service;

import no.vicx.database.user.UserImageRepository;
import no.vicx.database.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Stream;

import static no.vicx.backend.user.UserTestUtils.createMultipartFile;
import static no.vicx.backend.user.UserTestUtils.createValidVicxUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserImageServiceTest {

    @Mock
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserImageRepository userImageRepository;

    @InjectMocks
    UserImageService sut;

    @Test
    void addOrReplaceUserImage_givenExistingUser_expectImageAddedToUser() throws IOException {
        var userInTest = spy(createValidVicxUser());
        var fileInTest = spy(createMultipartFile("test-png.png", MediaType.IMAGE_PNG_VALUE));

        when(userService.getUserByUserName(anyString())).thenReturn(userInTest);

        sut.addOrReplaceUserImage(fileInTest, userInTest.getUsername());

        verify(fileInTest).getBytes();
        verify(fileInTest).getContentType();

        verify(userInTest).setUserImage(any());

        verify(userService).getUserByUserName(userInTest.getUsername());
        verify(userRepository).save(any());
    }

    @Test
    void addOrReplaceUserImage_givenGetBytesThrowsIOException_expectIOException() throws IOException {
        var fileInTest = mock(MultipartFile.class);

        when(fileInTest.getBytes()).thenThrow(IOException.class);

        assertThrows(IOException.class, () -> sut.addOrReplaceUserImage(fileInTest, "~username~"));

        verify(userService, never()).getUserByUserName(anyString());
    }

    private static Stream<Arguments> invalidFileSource() {
        return Stream.of(
                Arguments.of(new byte[]{1, 2, 3}, null),
                Arguments.of(null, MediaType.IMAGE_PNG_VALUE)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidFileSource")
    void addOrReplaceUserImage_givenInvalidFile_expectNullPointerException(
            byte[] bytes, String mediaType) throws IOException {

        var fileInTest = mock(MultipartFile.class);
        when(fileInTest.getBytes()).thenReturn(bytes);
        when(fileInTest.getContentType()).thenReturn(mediaType);

        assertThrows(NullPointerException.class, () -> sut.addOrReplaceUserImage(fileInTest, "~username~"));

        verify(userService, never()).getUserByUserName(anyString());
    }

    @Test
    void addOrReplaceUserImage_givenNonExistingUser_expectNullPointerException() {
        assertThrows(NullPointerException.class, () -> sut.addOrReplaceUserImage(
                createMultipartFile("test-jpg.jpg", MediaType.IMAGE_JPEG_VALUE),
                "~username~"));

        verify(userService).getUserByUserName(anyString());
    }

    @Test
    void deleteUserImage() {
        sut.deleteUserImage("user1");
        verify(userImageRepository).deleteByUserUsername("user1");
    }
}