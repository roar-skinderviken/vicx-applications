package no.vicx.backend.user.vm;

import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserPatchRequestVmTest {

    @Mock
    VicxUser vicxUser;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void applyPatch_givenFullyPopulatedRequest_expectUpdatedVicxUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("~encoded-password~");

        var sut = new UserPatchRequestVm("~password~", "~name~", "~email~");

        var returnedUser = sut.applyPatch(vicxUser, passwordEncoder);

        assertSame(vicxUser, returnedUser);
        verify(vicxUser).setPassword("~encoded-password~");
        verify(vicxUser).setName("~name~");
        verify(vicxUser).setEmail("~email~");

        verify(passwordEncoder).encode("~password~");
    }

    @Test
    void applyPatch_givenEmptyRequest_expectVicxUserNotToBeUpdated() {
        var sut = new UserPatchRequestVm(null, null, null);

        var returnedUser = sut.applyPatch(vicxUser, passwordEncoder);

        assertSame(vicxUser, returnedUser);
        verify(vicxUser, never()).setPassword(anyString());
        verify(vicxUser, never()).setName(anyString());
        verify(vicxUser, never()).setEmail(anyString());

        verify(passwordEncoder, never()).encode(anyString());
    }

    @ParameterizedTest
    @MethodSource("isEmptySource")
    void isEmpty(UserPatchRequestVm userPatchRequestVm, boolean expected) {
        assertEquals(expected, userPatchRequestVm.isEmpty());
    }

    private static Stream<Arguments> isEmptySource() {
        return Stream.of(
                Arguments.of(new UserPatchRequestVm("~password~", "~name~", "~email~"), false),
                Arguments.of(new UserPatchRequestVm("~password~", null, null), false),
                Arguments.of(new UserPatchRequestVm(null, "~name~", null), false),
                Arguments.of(new UserPatchRequestVm(null, null, "~email~"), false),
                Arguments.of(new UserPatchRequestVm(null, null, null), true)
        );
    }
}