package no.vicx.backend.user.vm;

import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangePasswordVmTest {

    @Mock
    VicxUser vicxUser;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void applyPatch_givenValidRequest_expectUpdatedVicxUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("~encoded-password~");

        var sut = new ChangePasswordVm("~current-password~", "~new-password~");

        var returnedUser = sut.applyPatch(vicxUser, passwordEncoder);

        assertSame(vicxUser, returnedUser);
        verify(vicxUser).setPassword("~encoded-password~");

        verify(passwordEncoder).encode("~new-password~");
    }
}