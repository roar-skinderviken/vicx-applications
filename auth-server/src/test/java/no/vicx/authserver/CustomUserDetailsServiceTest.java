package no.vicx.authserver;

import no.vicx.database.user.UserImage;
import no.vicx.database.user.UserRepository;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CustomUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    CustomUserDetailsService sut;

    @BeforeEach
    void setUp() {
        openMocks(this);

        sut = spy(new CustomUserDetailsService(userRepository, DEFAULT_USERNAME));

        when(userRepository.findByUsername(EXISTING_USERNAME))
                .thenReturn(Optional.of(createUserInTest()));
    }

    @Test
    void loadUserByUsername_givenUsernameForDefaultUser_expectDefaultUser() {
        sut.loadUserByUsername(DEFAULT_USERNAME);

        verify(sut).getDefaultUserDetails();
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void loadUserByUsername_givenUsernameForDefaultUserInUpperCase_expectDefaultUser() {
        sut.loadUserByUsername(DEFAULT_USERNAME.toUpperCase());
        verify(sut).getDefaultUserDetails();
    }

    @Test
    void loadUserByUsername_givenUsernameForExistingUser_expectUser() {
        sut.loadUserByUsername(DEFAULT_USERNAME);

        var userDetails = sut.loadUserByUsername(EXISTING_USERNAME);
        assertInstanceOf(CustomUserDetails.class, userDetails);

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        assertEquals(EXISTING_USERNAME, customUserDetails.getUsername());
        assertEquals("~password~", customUserDetails.getPassword());

        assertTrue(customUserDetails.getAuthorities().stream()
                .anyMatch(it -> it.getAuthority().equals("ROLE_USER")));

        assertEquals("~name~", customUserDetails.getName());
        assertEquals("~email~", customUserDetails.getEmail());
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

    static VicxUser createUserInTest() {
        var user = new VicxUser();
        user.setUsername(EXISTING_USERNAME);
        user.setPassword("~password~");
        user.setName("~name~");
        user.setEmail("~email~");

        var userImage = new UserImage();
        userImage.setImageData(new byte[]{1, 2, 3});
        userImage.setContentType(MediaType.IMAGE_PNG_VALUE);
        user.setUserImage(userImage);

        return user;
    }
}