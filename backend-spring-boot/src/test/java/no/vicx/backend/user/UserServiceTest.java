package no.vicx.backend.user;

import no.vicx.backend.error.NotFoundException;
import no.vicx.database.user.UserRepository;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

import static no.vicx.backend.user.UserTestUtils.*;
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

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#mockMultipartFileProvider")
    void createUser_givenValidUser_shouldCreateUserInDatabase(MockMultipartFile imageFile) throws IOException {
        var userVmInTest = createValidUserVm();
        var expectedUser = userVmInTest.toNewVicxUser();
        expectedUser.setPassword("encoded");

        when(passwordEncoder.encode("P4ssword")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(expectedUser);

        sut.createUser(userVmInTest, imageFile);

        verify(passwordEncoder, times(1)).encode("P4ssword");

        var userCaptor = ArgumentCaptor.forClass(VicxUser.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        var capturedUser = userCaptor.getValue();
        assertEquals("encoded", capturedUser.getPassword());

        if (imageFile == null || imageFile.isEmpty()) {
            assertNull(capturedUser.getUserImage());
        } else {
            assertNotNull(capturedUser.getUserImage());
        }
    }

    @Test
    void getUser_givenNonExistingUser_expectException() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sut.getUserByUserName("user1"));
    }

    @Test
    void getUser_givenExistingUser_expectUser() {
        var userInTest = createValidUser();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(userInTest));

        var userInDb = sut.getUserByUserName("user1");

        assertNotNull(userInDb);
    }

    @Test
    void updateUser_givenExistingUser_shouldUpdateUserInDatabase() {
        var userVmInTest = createValidUserVm();
        var userInTest = userVmInTest.toNewVicxUser();

        when(passwordEncoder.encode("P4ssword")).thenReturn("encoded");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(userInTest));
        when(userRepository.save(any())).thenReturn(userInTest);

        var updatedUser = sut.updateUser(userVmInTest);

        assertNotNull(updatedUser);
        assertEquals("encoded", updatedUser.getPassword());

        verify(passwordEncoder, times(1)).encode("P4ssword");
        verify(userRepository, times(1)).save(userInTest);
    }
}