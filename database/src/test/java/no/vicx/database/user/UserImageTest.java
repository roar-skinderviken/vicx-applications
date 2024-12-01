package no.vicx.database.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static no.vicx.database.user.RepositoryTestUtils.IMAGE_JPEG;
import static no.vicx.database.user.UserImage.CONTENT_TYPE_MUST_NOT_BE_NULL;
import static no.vicx.database.user.UserImage.IMAGE_DATA_MUST_NOT_BE_NULL;
import static org.junit.jupiter.api.Assertions.*;

class UserImageTest {

    @Test
    void constructor_givenValidValues_expectPopulatedInstance() {
        var sut = new UserImage(validImageData, IMAGE_JPEG);

        assertEquals(validImageData, sut.getImageData());
        assertEquals(IMAGE_JPEG, sut.getContentType());

        assertNull(sut.getId());
        assertNull(sut.getUser());
    }

    @ParameterizedTest
    @MethodSource("invalidImageDataSource")
    void constructor_givenInvalidValues_expectNullPointerException(
            byte[] imageData, String contentType, String expectedMessage) {

        var exception =
                assertThrows(NullPointerException.class, () -> new UserImage(imageData, contentType));

        assertEquals(expectedMessage, exception.getMessage());
    }

    private static final byte[] validImageData = new byte[]{1, 2, 3};

    private static Stream<Arguments> invalidImageDataSource() {
        return Stream.of(
                Arguments.of(null, IMAGE_JPEG, IMAGE_DATA_MUST_NOT_BE_NULL),
                Arguments.of(validImageData, null, CONTENT_TYPE_MUST_NOT_BE_NULL)
        );
    }
}