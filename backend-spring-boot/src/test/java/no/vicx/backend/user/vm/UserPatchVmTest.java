package no.vicx.backend.user.vm;

import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class UserPatchVmTest {

    @Mock
    VicxUser vicxUser;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void applyPatch_givenFullyPopulatedRequest_expectUpdatedVicxUser() {
        var sut = new UserPatchVm("~name~", "~email~");

        var returnedUser = sut.applyPatch(vicxUser);

        assertSame(vicxUser, returnedUser);
        verify(vicxUser).setName("~name~");
        verify(vicxUser).setEmail("~email~");
    }

    @Test
    void applyPatch_givenEmptyRequest_expectVicxUserNotToBeUpdated() {
        var sut = new UserPatchVm(null, null);

        var returnedUser = sut.applyPatch(vicxUser);

        assertSame(vicxUser, returnedUser);
        verify(vicxUser, never()).setName(anyString());
        verify(vicxUser, never()).setEmail(anyString());
    }

    @ParameterizedTest
    @MethodSource("isEmptySource")
    void isEmpty(UserPatchVm userPatchVm, boolean expected) {
        assertEquals(expected, userPatchVm.isEmpty());
    }

    private static Stream<Arguments> isEmptySource() {
        return Stream.of(
                Arguments.of(new UserPatchVm("~name~", "~email~"), false),
                Arguments.of(new UserPatchVm(null, null), true),
                Arguments.of(new UserPatchVm("~name~", null), false),
                Arguments.of(new UserPatchVm(null, "~email~"), false)
        );
    }
}