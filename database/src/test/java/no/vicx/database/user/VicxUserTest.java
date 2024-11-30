package no.vicx.database.user;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static no.vicx.database.user.VicxUser.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VicxUserTest {
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void constructor_givenValidValues_expectPopulatedInstance(boolean addImage) {
        var image = addImage ? mock(UserImage.class) : null;

        var sut = new VicxUser(userName, password, name, email, image);

        assertEquals(userName, sut.getUsername());
        assertEquals(password, sut.getPassword());
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
            String username, String password, String name, String email, String expectedMessage) {

        var exception =
                assertThrows(NullPointerException.class, () -> new VicxUser(username, password, name, email, null));

        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> invalidValuesSource() {
        return Stream.of(
                Arguments.of(null, password, name, email, USERNAME_MUST_NOT_BE_NULL),
                Arguments.of(userName, null, name, email, PASSWORD_MUST_NOT_BE_NULL),
                Arguments.of(userName, password, null, email, NAME_MUST_NOT_BE_NULL),
                Arguments.of(userName, password, name, null, EMAIL_MUST_NOT_BE_NULL)
        );
    }

    private static final String userName = "~username~";
    private static final String password = "~password~";
    private static final String name = "~name~";
    private static final String email = "~email~";
}