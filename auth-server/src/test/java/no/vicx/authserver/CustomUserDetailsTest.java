package no.vicx.authserver;

import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.util.stream.Stream;

import static no.vicx.authserver.CustomUserDetails.*;
import static no.vicx.authserver.UserTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CustomUserDetailsTest {

    @Mock
    VicxUser user;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @ParameterizedTest
    @MethodSource("invalidParametersSource")
    void constructor_givenInvalidParameters_expectException(
            String username, String password, String name, String email,
            Class<? extends Exception> expectedException, String expectedMessage) {
        var exception = assertThrows(expectedException, () ->
                new CustomUserDetails(username, password, name, email, false));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidParametersSource")
    void constructor_givenInvalidVicxUser_expectIllegalArgumentException(
            String username, String password, String name, String email,
            Class<? extends Exception> expectedException, String expectedMessage) {
        when(user.getUsername()).thenReturn(username);
        when(user.getPassword()).thenReturn(password);
        when(user.getName()).thenReturn(name);
        when(user.getEmail()).thenReturn(email);
        when(user.getUserImage()).thenReturn(null);

        var exception = assertThrows(expectedException, () -> new CustomUserDetails(user));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void constructor_givenNullVicxUser_expectException() {
        //noinspection DataFlowIssue
        assertThrows(NullPointerException.class, () -> new CustomUserDetails(null));
    }

    @Test
    void constructor_givenValidVicxUserWithoutImage_expectPopulatedInstance() {
        var vicxUserInTest = createUserInTest(null);
        var customUserDetails = new CustomUserDetails(vicxUserInTest);

        assertEquals(vicxUserInTest.getUsername(), customUserDetails.getUsername());
        assertEquals(vicxUserInTest.getPassword(), customUserDetails.getPassword());
        assertEquals(vicxUserInTest.getName(), customUserDetails.getName());
        assertEquals(vicxUserInTest.getEmail(), customUserDetails.getEmail());
        assertFalse(customUserDetails.hasImage());
        assertTrue(customUserDetails.getAuthorities().containsAll(GRANTED_AUTHORITIES));

        assertTrue(customUserDetails.isEnabled());
        assertTrue(customUserDetails.isAccountNonExpired());
        assertTrue(customUserDetails.isAccountNonLocked());
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    @Test
    void constructor_givenValidVicxUserWithImage_expectInstanceWithImage() {
        var customUserDetails = new CustomUserDetails(createUserInTest(createUserImageInTest()));

        assertTrue(customUserDetails.hasImage());
    }

    static Stream<Arguments> invalidParametersSource() {
        return Stream.of(
                Arguments.of(null, "~password~", "~name~", "~email~", IllegalArgumentException.class, USER_BASECLASS_GUARD_MSG),
                Arguments.of("", "~password~", "~name~", "~email~", IllegalArgumentException.class, USER_BASECLASS_GUARD_MSG),
                Arguments.of("~username~", null, "~name~", "~email~", IllegalArgumentException.class, USER_BASECLASS_GUARD_MSG),
                Arguments.of("~username~", "~password~", null, "~email~", NullPointerException.class, NAME_NOT_NULL_MSG),
                Arguments.of("~username~", "~password~", "~name~", null, NullPointerException.class, EMAIL_NOT_NULL_MSG)
        );
    }

    static final String USER_BASECLASS_GUARD_MSG = "Cannot pass null or empty values to constructor";
}