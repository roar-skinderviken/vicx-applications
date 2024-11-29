package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import no.vicx.database.user.UserRepository;
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
    DefaultUserProperties defaultUserProperties;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    UserDetailsConfig userDetailsConfig;
    UserDetailsService userDetailsService;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);

        when(passwordEncoder.encode(anyString())).thenReturn("~encoded-password~");

        when(defaultUserProperties.username()).thenReturn(DEFAULT_USERNAME_IN_TEST);
        when(defaultUserProperties.password()).thenReturn("~password~");
        when(defaultUserProperties.email()).thenReturn("~email~");
        when(defaultUserProperties.name()).thenReturn("~name~");

        when(userRepository.findByUsername(EXISTING_USERNAME))
                .thenReturn(Optional.of(createUserInTest(null)));

        userDetailsConfig = spy(new UserDetailsConfig(defaultUserProperties, passwordEncoder));
        userDetailsService = userDetailsConfig.userDetailsService(userRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void loadUserByUsername_givenUsernameForDefaultUser_expectDefaultUser() {
        userDetailsService.loadUserByUsername(DEFAULT_USERNAME_IN_TEST);

        verify(userDetailsConfig).createDefaultUser();
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void loadUserByUsername_givenUsernameForDefaultUserInUpperCase_expectDefaultUser() {
        userDetailsService.loadUserByUsername(DEFAULT_USERNAME_IN_TEST.toUpperCase());

        verify(userDetailsConfig).createDefaultUser();
    }

    @Test
    void loadUserByUsername_givenUsernameForExistingUser_expectUser() {
        var userDetails = userDetailsService.loadUserByUsername(EXISTING_USERNAME);

        assertInstanceOf(CustomUserDetails.class, userDetails);

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        assertEquals(EXISTING_USERNAME, customUserDetails.getUsername());
        assertEquals("~password~", customUserDetails.getPassword());
        assertEquals("~name~", customUserDetails.getName());
        assertEquals("~email~", customUserDetails.getEmail());
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
    void defaultUser_givenUsernameForExistingUser_expectUser() {
        var defaultUser = userDetailsConfig.createDefaultUser();

        assertEquals(DEFAULT_USERNAME_IN_TEST, defaultUser.getUsername());
        assertEquals("~encoded-password~", defaultUser.getPassword());
        assertEquals("~email~", defaultUser.getEmail());
        assertEquals("~name~", defaultUser.getName());
        assertFalse(defaultUser.hasImage());

        assertTrue(defaultUser.getAuthorities().containsAll(GRANTED_AUTHORITIES));
    }
}