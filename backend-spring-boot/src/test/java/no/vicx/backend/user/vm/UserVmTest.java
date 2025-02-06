package no.vicx.backend.user.vm;

import org.junit.jupiter.api.Test;

import static no.vicx.backend.user.UserTestUtils.createValidVicxUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserVmTest {

    @Test
    void fromVicxUser_givenFullyPopulatedUser_expectPopulatedTargetWithoutPassword() {
        var vicxUser = createValidVicxUser();

        var target = UserVm.fromVicxUser(vicxUser);

        assertNotNull(target);
        assertEquals(vicxUser.getUsername(), target.username());
        assertEquals(vicxUser.getName(), target.name());
        assertEquals(vicxUser.getEmail(), target.email());
    }
}