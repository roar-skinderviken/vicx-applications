package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import no.vicx.database.user.UserImage;
import no.vicx.database.user.UserRepository;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

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

    UserDetailsService sut;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);
        sut = new UserDetailsConfig().userDetailsService(
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
        sut.loadUserByUsername(DEFAULT_USERNAME);

        verify(defaultUserFactory).getObject();
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void loadUserByUsername_givenUsernameForDefaultUserInUpperCase_expectDefaultUser() {
        sut.loadUserByUsername(DEFAULT_USERNAME.toUpperCase());
        verify(defaultUserFactory).getObject();
    }

    @Test
    void loadUserByUsername_givenUsernameForExistingUser_expectUser() {
        var userDetails = sut.loadUserByUsername(EXISTING_USERNAME);

        assertInstanceOf(CustomUserDetails.class, userDetails);

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        assertEquals(EXISTING_USERNAME, customUserDetails.getUsername());
        assertEquals("~password~", customUserDetails.getPassword());

        assertTrue(customUserDetails.getAuthorities().stream()
                .anyMatch(it -> it.getAuthority().equals("ROLE_USER")));

        assertEquals("~name~", customUserDetails.getName());
        assertEquals("~email~", customUserDetails.getEmail());
        assertFalse(customUserDetails.hasImage());
    }

    @Test
    void loadUserByUsername_givenUsernameForExistingUserWithImage_expectUserWithImage() {
        when(userRepository.findByUsername(EXISTING_USERNAME))
                .thenReturn(Optional.of(createUserInTest(createUserImageInTest())));

        var customUserDetails = (CustomUserDetails) sut.loadUserByUsername(EXISTING_USERNAME);

        assertTrue(customUserDetails.hasImage());
    }

    @Test
    void loadUserByUsername_givenUsernameForNonExistingUser_expectUsernameNotFoundException() {
        assertThrows(UsernameNotFoundException.class, () ->
                sut.loadUserByUsername(NON_EXISTING_USERNAME));
    }

    static final String DEFAULT_USERNAME = "user1";
    static final String EXISTING_USERNAME = "~username~";
    static final String NON_EXISTING_USERNAME = "user2";

    static UserImage createUserImageInTest() {
        var userImage = new UserImage();
        userImage.setImageData(new byte[]{1, 2, 3});
        userImage.setContentType(MediaType.IMAGE_PNG_VALUE);
        return userImage;
    }

    static VicxUser createUserInTest(UserImage userImage) {
        var user = new VicxUser();
        user.setUsername(EXISTING_USERNAME);
        user.setPassword("~password~");
        user.setName("~name~");
        user.setEmail("~email~");

        if (userImage != null) {
            user.setUserImage(userImage);
        }
        return user;
    }
}