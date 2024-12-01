package no.vicx.backend.user.service;

import no.vicx.backend.error.NotFoundException;
import no.vicx.backend.user.vm.UserPatchRequestVm;
import no.vicx.database.user.UserRepository;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.AfterEach;
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
import static no.vicx.database.user.VicxUser.VALID_BCRYPT_PASSWORD;
import static no.vicx.database.user.VicxUser.VALID_PLAINTEXT_PASSWORD;
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

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#mockMultipartFileProvider")
    void createUser_givenValidUser_shouldCreateUserInDatabase(MockMultipartFile imageFile) throws IOException {
        var expectedUser = createValidVicxUser();
        expectedUser.setPassword(VALID_BCRYPT_PASSWORD);

        when(passwordEncoder.encode(VALID_PLAINTEXT_PASSWORD)).thenReturn(VALID_BCRYPT_PASSWORD);
        when(userRepository.save(any())).thenReturn(expectedUser);

        sut.createUser(VALID_USER_VM, imageFile);

        verify(passwordEncoder, times(1)).encode(VALID_PLAINTEXT_PASSWORD);

        var userCaptor = ArgumentCaptor.forClass(VicxUser.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        var capturedUser = userCaptor.getValue();
        assertEquals(VALID_BCRYPT_PASSWORD, capturedUser.getPassword());

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
        var userInTest = createValidVicxUser();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(userInTest));

        var userInDb = sut.getUserByUserName("user1");

        assertNotNull(userInDb);
    }

    @Test
    void updateUser_givenExistingUser_shouldUpdateUserInDatabase() {
        var patchVm = new UserPatchRequestVm(VALID_PLAINTEXT_PASSWORD, "~name~", "foo@bar.com");
        var userInTest = createValidVicxUser();

        when(passwordEncoder.encode(VALID_PLAINTEXT_PASSWORD)).thenReturn(VALID_BCRYPT_PASSWORD);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(userInTest));
        when(userRepository.save(any())).thenReturn(userInTest);

        sut.updateUser(patchVm, "user1");

        verify(passwordEncoder).encode(VALID_PLAINTEXT_PASSWORD);
        verify(userRepository).save(userInTest);
    }
}