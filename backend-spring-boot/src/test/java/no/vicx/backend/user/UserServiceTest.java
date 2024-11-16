package no.vicx.backend.user;

import no.vicx.backend.error.NotFoundException;
import no.vicx.database.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static no.vicx.backend.user.UserTestUtils.VALID_USER_VM;
import static no.vicx.backend.user.UserTestUtils.createValidVicxUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService sut;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void createUser_givenValidUser_shouldCreateUserInDatabase() {
        var userInTest = createValidVicxUser();

        when(passwordEncoder.encode("P4ssword")).thenReturn("encoded");
        when(userRepository.save(userInTest)).thenReturn(userInTest);

        sut.createUser(userInTest);

        assertEquals("encoded", userInTest.getPassword());

        verify(passwordEncoder, times(1)).encode("P4ssword");
        verify(userRepository, times(1)).save(userInTest);
    }

    @Test
    void getUser_givenNonExistingUser_expectException() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sut.getUserByUserName("user1"));
    }

    @Test
    void getUser_givenExistingUser_expectUser() {
        var userInTest = createValidVicxUser();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(userInTest));

        var userInDb = sut.getUserByUserName("user1");

        assertNotNull(userInDb);
    }

    @Test
    void updateUser_givenExistingUser_shouldUpdateUserInDatabase() {
        var userInTest = createValidVicxUser();

        when(passwordEncoder.encode("P4ssword")).thenReturn("encoded");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(userInTest));
        when(userRepository.save(any())).thenReturn(userInTest);

        var updatedUser = sut.updateUser(VALID_USER_VM);

        assertNotNull(updatedUser);
        assertEquals("encoded", updatedUser.getPassword());

        verify(passwordEncoder, times(1)).encode("P4ssword");
        verify(userRepository, times(1)).save(userInTest);
    }
}