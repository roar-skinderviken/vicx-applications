package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import no.vicx.database.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static no.vicx.authserver.CustomUserDetails.GRANTED_AUTHORITIES;
import static no.vicx.authserver.UserTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserDetailsConfigTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ObjectFactory<CustomUserDetails> defaultUserFactory;

    UserDetailsService userDetailsService;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);
        userDetailsService = new UserDetailsConfig().userDetailsService(
                "user1", defaultUserFactory, userRepository);

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(EXISTING_USERNAME))
                .thenReturn(Optional.of(createUserInTest(null)));
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void loadUserByUsername_givenUsernameForDefaultUser_expectDefaultUser() {
        userDetailsService.loadUserByUsername(DEFAULT_USERNAME);

        verify(defaultUserFactory).getObject();
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void loadUserByUsername_givenUsernameForDefaultUserInUpperCase_expectDefaultUser() {
        userDetailsService.loadUserByUsername(DEFAULT_USERNAME.toUpperCase());
        verify(defaultUserFactory).getObject();
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

    @Mock
    DefaultUserProperties userProperties;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void defaultUser_givenUsernameForExistingUser_expectUser() {
        when(userProperties.username()).thenReturn("~username~");
        when(userProperties.password()).thenReturn("~password~");
        when(userProperties.email()).thenReturn("~email~");
        when(userProperties.name()).thenReturn("~name~");

        when(passwordEncoder.encode(anyString())).thenReturn("~encoded-password~");

        var defaultUser = new UserDetailsConfig().defaultUser(userProperties, passwordEncoder);

        assertEquals("~username~", defaultUser.getUsername());
        assertEquals("~encoded-password~", defaultUser.getPassword());
        assertEquals("~email~", defaultUser.getEmail());
        assertEquals("~name~", defaultUser.getName());
        assertFalse(defaultUser.hasImage());

        assertTrue(defaultUser.getAuthorities().containsAll(GRANTED_AUTHORITIES));
    }
}