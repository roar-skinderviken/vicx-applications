package no.vicx.database.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static no.vicx.database.user.RepositoryTestUtils.*;
import static no.vicx.database.user.VicxUser.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class VicxUserTest {

    @Test
    void setPassword_givenNullPassword_expectNullPointerException() {
        var exception =
                assertThrows(NullPointerException.class, () -> new VicxUser().setPassword(null));

        assertEquals(PASSWORD_MUST_NOT_BE_NULL, exception.getMessage());
    }

    @Test
    void setPassword_givenPlainTextPassword_expectIllegalArgumentException() {
        var exception =
                assertThrows(IllegalArgumentException.class, () -> new VicxUser().setPassword("~password~"));

        assertEquals(PASSWORD_MUST_BE_ENCRYPTED, exception.getMessage());
    }

    @Test
    void setPassword_givenEncryptedPassword_expectPasswordToBeSet() {
        var sut = new VicxUser();
        sut.setPassword(VALID_BCRYPT_PASSWORD);
        assertEquals(VALID_BCRYPT_PASSWORD, sut.getPassword());
    }

    @Test
    void setUserImage_givenUserWithImage_expectUserWithoutImage() throws IOException {
        var sut = createValidUser(createPngUserImage());

        sut.setUserImage(null);

        assertNull(sut.getUserImage());
    }

    @Test
    void setUserImage_givenUserWithImage_expectUpdatedImage() throws IOException {
        var sut = createValidUser(createPngUserImage());

        sut.setUserImage(createJpegUserImage());

        assertEquals(IMAGE_JPEG, sut.getUserImage().getContentType());
    }

    @Test
    void setUserImage_givenUserWithoutImage_expectAddedImage() {
        var mockImage = mock(UserImage.class);
        var sut = createValidUser(null);

        sut.setUserImage(mockImage);

        verify(mockImage).setUser(sut);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void constructor_givenValidValues_expectPopulatedInstance(boolean addImage) {
        var image = addImage ? mock(UserImage.class) : null;

        var sut = VicxUser.builder()
                .username(userName)
                .password(VALID_BCRYPT_PASSWORD)
                .name(name)
                .email(email)
                .userImage(image)
                .build();

        assertEquals(userName, sut.getUsername());
        assertEquals(VALID_BCRYPT_PASSWORD, sut.getPassword());
        assertEquals(name, sut.getName());
        assertEquals(email, sut.getEmail());

        if (addImage) {
            assertNotNull(image);
            assertEquals(image, sut.getUserImage());
        } else {
            assertNull(sut.getUserImage());
        }

        assertNull(sut.getId());
    }

    @ParameterizedTest
    @MethodSource("invalidValuesSource")
    void constructor_givenInvalidValues_expectIllegalArgumentException(
            String username, String password, String name, String email,
            String expectedMessage, Class<? extends Exception> expectedException) {

        var exception =
                assertThrows(expectedException, () -> new VicxUser(username, password, name, email, null));

        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> invalidValuesSource() {
        return Stream.of(
                Arguments.of(null, VALID_BCRYPT_PASSWORD, name, email, USERNAME_MUST_NOT_BE_NULL, NullPointerException.class),
                Arguments.of(userName, null, name, email, PASSWORD_MUST_NOT_BE_NULL, NullPointerException.class),
                Arguments.of(userName, "~password~", name, email, PASSWORD_MUST_BE_ENCRYPTED, IllegalArgumentException.class),
                Arguments.of(userName, VALID_BCRYPT_PASSWORD, null, email, NAME_MUST_NOT_BE_NULL, NullPointerException.class),
                Arguments.of(userName, VALID_BCRYPT_PASSWORD, name, null, EMAIL_MUST_NOT_BE_NULL, NullPointerException.class)
        );
    }
    
    private static final String userName = "~username~";
    private static final String name = "~name~";
    private static final String email = "~email~";
}