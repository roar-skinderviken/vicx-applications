package no.vicx.authserver;

import org.junit.jupiter.api.Test;

import static no.vicx.authserver.CustomUserDetails.GRANTED_AUTHORITIES;
import static no.vicx.authserver.UserTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void constructor_givenValidParameters_expectPopulatedInstance() {
        var userDetails = new CustomUserDetails(
                "~username~",
                "~password~",
                "~name~",
                "~email~",
                true
        );

        assertEquals("~username~", userDetails.getUsername());
        assertEquals("~password~", userDetails.getPassword());
        assertEquals("~name~", userDetails.getName());
        assertEquals("~email~", userDetails.getEmail());
        assertTrue(userDetails.isEnabled());

        assertEquals(GRANTED_AUTHORITIES.size(), userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().containsAll(GRANTED_AUTHORITIES));
    }

    @Test
    void constructor_givenValidVicxUserWithoutImage_expectPopulatedInstance() {
        var customUserDetails = new CustomUserDetails(createUserInTest(null));

        assertEquals(EXISTING_USERNAME, customUserDetails.getUsername());
        assertEquals("~password~", customUserDetails.getPassword());
        assertEquals("~name~", customUserDetails.getName());
        assertEquals("~email~", customUserDetails.getEmail());
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
}