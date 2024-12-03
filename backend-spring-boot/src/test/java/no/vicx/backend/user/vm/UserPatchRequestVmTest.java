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

class UserPatchRequestVmTest {

    @Mock
    VicxUser vicxUser;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void applyPatch_givenFullyPopulatedRequest_expectUpdatedVicxUser() {
        var sut = new UserPatchRequestVm("~name~", "~email~");

        var returnedUser = sut.applyPatch(vicxUser);

        assertSame(vicxUser, returnedUser);
        verify(vicxUser).setName("~name~");
        verify(vicxUser).setEmail("~email~");
    }

    @Test
    void applyPatch_givenEmptyRequest_expectVicxUserNotToBeUpdated() {
        var sut = new UserPatchRequestVm(null, null);

        var returnedUser = sut.applyPatch(vicxUser);

        assertSame(vicxUser, returnedUser);
        verify(vicxUser, never()).setName(anyString());
        verify(vicxUser, never()).setEmail(anyString());
    }

    @ParameterizedTest
    @MethodSource("isEmptySource")
    void isEmpty(UserPatchRequestVm userPatchRequestVm, boolean expected) {
        assertEquals(expected, userPatchRequestVm.isEmpty());
    }

    private static Stream<Arguments> isEmptySource() {
        return Stream.of(
                Arguments.of(new UserPatchRequestVm("~name~", "~email~"), false),
                Arguments.of(new UserPatchRequestVm(null, null), true),
                Arguments.of(new UserPatchRequestVm("~name~", null), false),
                Arguments.of(new UserPatchRequestVm(null, "~email~"), false)
        );
    }
}