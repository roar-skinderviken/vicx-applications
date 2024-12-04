package no.vicx.backend.user.service;

import no.vicx.backend.error.NotFoundException;
import no.vicx.backend.user.vm.ChangePasswordVm;
import no.vicx.backend.user.vm.UserPatchVm;
import no.vicx.database.user.UserRepository;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

import static no.vicx.backend.user.UserTestUtils.VALID_USER_VM;
import static no.vicx.backend.user.UserTestUtils.createValidVicxUser;
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

    // START createUser

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#mockMultipartFileProvider")
    void createUser_givenValidUser_shouldCreateUserInDatabase(MockMultipartFile imageFile) throws IOException {
        var expectedUser = createValidVicxUser();
        expectedUser.setPassword(VALID_BCRYPT_PASSWORD);

        when(passwordEncoder.encode(VALID_PLAINTEXT_PASSWORD)).thenReturn(VALID_BCRYPT_PASSWORD);
        when(userRepository.save(any())).thenReturn(expectedUser);

        sut.createUser(VALID_USER_VM, imageFile);

        verify(passwordEncoder).encode(VALID_PLAINTEXT_PASSWORD);

        var userCaptor = ArgumentCaptor.forClass(VicxUser.class);
        verify(userRepository).save(userCaptor.capture());

        var capturedUser = userCaptor.getValue();
        assertEquals(VALID_BCRYPT_PASSWORD, capturedUser.getPassword());

        if (imageFile == null || imageFile.isEmpty()) {
            assertNull(capturedUser.getUserImage());
        } else {
            assertNotNull(capturedUser.getUserImage());
        }
    }

    // START getUser

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

    // START updateUser

    @Test
    void updateUser_givenExistingUser_shouldUpdateUserInDatabase() {
        var patchVm = new UserPatchVm("~name~", "foo@bar.com");
        var userInTest = createValidVicxUser();

        when(passwordEncoder.encode(VALID_PLAINTEXT_PASSWORD)).thenReturn(VALID_BCRYPT_PASSWORD);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(userInTest));
        when(userRepository.save(any())).thenReturn(userInTest);

        sut.updateUser(patchVm, "user1");

        verify(userRepository).save(userInTest);
    }

    // START isValidPassword

    @Test
    void isValidPassword_givenNonExistingUser_expectException() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                sut.isValidPassword("user1", "~clear-text-password~"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isValidPassword(boolean expected) {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(createValidVicxUser()));
        when(passwordEncoder.matches(VALID_PLAINTEXT_PASSWORD, VALID_BCRYPT_PASSWORD)).thenReturn(expected);

        assertEquals(expected, sut.isValidPassword("user1", VALID_PLAINTEXT_PASSWORD));
    }

    // START updatePassword

    @Test
    void updatePassword_givenExistingUser_() {
        var changePasswordVm = new ChangePasswordVm("~current-password~", VALID_PLAINTEXT_PASSWORD);
        var userInTest = createValidVicxUser();

        when(passwordEncoder.encode(VALID_PLAINTEXT_PASSWORD)).thenReturn(VALID_BCRYPT_PASSWORD);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(userInTest));
        when(userRepository.save(any())).thenReturn(userInTest);

        sut.updatePassword(changePasswordVm, "user1");

        verify(userRepository).save(userInTest);
    }
}