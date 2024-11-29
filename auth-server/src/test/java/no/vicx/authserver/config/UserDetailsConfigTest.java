package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import no.vicx.database.user.UserRepository;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static no.vicx.authserver.CustomUserDetails.GRANTED_AUTHORITIES;
import static no.vicx.authserver.UserTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserDetailsConfigTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    VicxUser userInTest;

    UserDetailsService userDetailsService;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);

        when(passwordEncoder.encode(anyString())).thenReturn("~encoded-password~");

        userInTest = createUserInTest(null);
        when(userRepository.findByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(userInTest));

        userDetailsService = new UserDetailsConfig().userDetailsService(
                DEFAULT_USER_PROPERTIES, passwordEncoder, userRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void loadUserByUsername_givenUsernameForDefaultUser_expectDefaultUser() {
        var defaultUser = (CustomUserDetails) userDetailsService.loadUserByUsername(DEFAULT_USERNAME_IN_TEST);

        assertEquals(DEFAULT_USER_PROPERTIES.username(), defaultUser.getUsername());
        assertEquals("~encoded-password~", defaultUser.getPassword());
        assertEquals(DEFAULT_USER_PROPERTIES.email(), defaultUser.getEmail());
        assertEquals(DEFAULT_USER_PROPERTIES.name(), defaultUser.getName());
        assertFalse(defaultUser.hasImage());
        assertTrue(defaultUser.getAuthorities().containsAll(GRANTED_AUTHORITIES));

        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void loadUserByUsername_givenUsernameForDefaultUserInUpperCase_expectDefaultUser() {
        var defaultUser = userDetailsService.loadUserByUsername(DEFAULT_USERNAME_IN_TEST.toUpperCase());

        assertEquals(DEFAULT_USERNAME_IN_TEST, defaultUser.getUsername());
    }

    @Test
    void loadUserByUsername_givenUsernameForExistingUser_expectUser() {
        var userDetails = userDetailsService.loadUserByUsername(EXISTING_USERNAME);

        assertInstanceOf(CustomUserDetails.class, userDetails);

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        assertEquals(userInTest.getUsername(), customUserDetails.getUsername());
        assertEquals(userInTest.getPassword(), customUserDetails.getPassword());
        assertEquals(userInTest.getName(), customUserDetails.getName());
        assertEquals(userInTest.getEmail(), customUserDetails.getEmail());
        assertFalse(customUserDetails.hasImage());
        assertTrue(customUserDetails.getAuthorities().containsAll(GRANTED_AUTHORITIES));
    }

    @Test
    void loadUserByUsername_givenUsernameForExistingUserWithImage_expectUserWithImage() {
        when(userRepository.findByUsername(EXISTING_USERNAME))
                .thenReturn(Optional.of(createUserInTest(createUserImageInTest())));

        var customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(EXISTING_USERNAME);

        assertTrue(customUserDetails.hasImage());
    }

    @Test
    void loadUserByUsername_givenUsernameForNonExistingUser_expectUsernameNotFoundException() {
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(NON_EXISTING_USERNAME));
    }

    @Test
    void loadUserByUsername_givenNullUsername_expectNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                userDetailsService.loadUserByUsername(null));
    }

    static final DefaultUserProperties DEFAULT_USER_PROPERTIES = new DefaultUserProperties(
            DEFAULT_USERNAME_IN_TEST, "~default-user-password~",
            "~default-user-name~", "~default-user-email~");
}