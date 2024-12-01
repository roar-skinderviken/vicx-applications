package no.vicx.backend.user.vm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import static no.vicx.backend.user.UserTestUtils.VALID_USER_VM;
import static no.vicx.backend.user.UserTestUtils.createValidVicxUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UserVmTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void toNewVicxUser_givenFullyPopulatedVm_expectPopulatedTarget() {
        when(passwordEncoder.encode(anyString())).thenReturn("~encoded-password~");

        var vicxUser = VALID_USER_VM.toNewVicxUser(passwordEncoder);

        assertNotNull(vicxUser);
        assertEquals(VALID_USER_VM.username(), vicxUser.getUsername());
        assertEquals("~encoded-password~", vicxUser.getPassword());
        assertEquals(VALID_USER_VM.name(), vicxUser.getName());
        assertEquals(VALID_USER_VM.email(), vicxUser.getEmail());

        assertNull(vicxUser.getId());
        assertNull(vicxUser.getUserImage());

        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void fromVicxUser_givenFullyPopulatedUser_expectPopulatedTargetWithoutPassword() {
        var vicxUser = createValidVicxUser();

        var target = UserVm.fromVicxUser(vicxUser);

        assertNotNull(target);
        assertEquals(vicxUser.getUsername(), target.username());
        assertNull(target.password());
        assertEquals(vicxUser.getName(), target.name());
        assertEquals(vicxUser.getEmail(), target.email());
    }
}