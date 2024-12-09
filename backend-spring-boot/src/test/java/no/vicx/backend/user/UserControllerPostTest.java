package no.vicx.backend.user;

import no.vicx.backend.BaseWebMvcTest;
import no.vicx.backend.error.ApiError;
import no.vicx.backend.user.service.RecaptchaService;
import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.vm.CreateUserVm;
import no.vicx.database.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static no.vicx.backend.user.UserController.USER_CREATED_BODY_TEXT;
import static no.vicx.backend.user.UserTestUtils.*;
import static no.vicx.database.user.VicxUser.VALID_PLAINTEXT_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerPostTest extends BaseWebMvcTest {

    @MockitoBean
    RecaptchaService recaptchaService;

    @MockitoBean
    UserService userService;

    @MockitoBean
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        when(recaptchaService.verifyToken(anyString())).thenReturn(true);
    }

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#mockMultipartFileProvider")
    void postUser_givenValidUser_expectCreated(MockMultipartFile imageFile) throws Exception {
        var validVicxUser = createValidVicxUser();
        validVicxUser.setId(42L);

        when(userService.createUser(any(), any())).thenReturn(validVicxUser);

        mockMvc.perform(createMultipartRequest(VALID_USER_VM, imageFile))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/user"))
                .andExpect(content().string(USER_CREATED_BODY_TEXT));

        var userCaptor = ArgumentCaptor.forClass(CreateUserVm.class);
        var imageCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(userService).createUser(userCaptor.capture(), imageCaptor.capture());

        var capturedUser = userCaptor.getValue();
        assertEquals(validVicxUser.getUsername(), capturedUser.username());

        var capturedImage = imageCaptor.getValue();
        if (capturedImage != null) {
            assertEquals(imageFile.getOriginalFilename(), capturedImage.getOriginalFilename());
        }

        // make sure RecaptchaThenUniqueUsername validator is executed just once
        verify(recaptchaService).verifyToken(anyString());
        verify(userRepository).findByUsername(anyString());
    }

    @ParameterizedTest
    @MethodSource("invalidUserVmProvider")
    void postUser_givenInvalidUser_expectBadRequest(
            CreateUserVm createUserVm, String fieldName, String expectedMessage) throws Exception {

        var apiError = performBadRequest(createUserVm, null);
        assertEquals(expectedMessage, apiError.validationErrors().get(fieldName));
    }

    @Test
    void postUser_GivenInvalidReCaptcha_expectBadRequest() throws Exception {
        when(recaptchaService.verifyToken(anyString())).thenReturn(false);

        var apiError = performBadRequest(VALID_USER_VM, null);

        assertEquals(
                "Invalid reCAPTCHA, please wait to token expires and try again",
                apiError.validationErrors().get("recaptchaToken"));

        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void postUser_givenDuplicateUsername_expectBadRequest() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(createValidVicxUser()));

        var apiError = performBadRequest(VALID_USER_VM, null);

        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.status());
        assertEquals("validation error", apiError.message());
        assertEquals("This name is in use", apiError.validationErrors().get("username"));

        verify(recaptchaService).verifyToken(anyString());
    }

    @ParameterizedTest
    @MethodSource("invalidImageProvider")
    void postUser_givenInvalidImage_expectBadRequest(
            MockMultipartFile imageFile, String fieldName, String expectedMessage) throws Exception {

        var apiError = performBadRequest(VALID_USER_VM, imageFile);
        assertEquals(expectedMessage, apiError.validationErrors().get(fieldName));
    }

    static Stream<Arguments> invalidImageProvider() throws IOException {
        return Stream.of(
                Arguments.of(
                        createMultipartFile("test-gif.gif", MediaType.IMAGE_GIF_VALUE),
                        "image", "Only PNG and JPG files are allowed"
                ),
                Arguments.of(
                        createMultipartFile("too-large.png", MediaType.IMAGE_PNG_VALUE),
                        "image", "File size exceeds the maximum allowed size of 51200 bytes"
                )
        );
    }

    static Stream<Arguments> invalidUserVmProvider() {
        return Stream.of(
                Arguments.of(
                        new CreateUserVm(null, VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                        "username", "Username cannot be null"
                ),
                Arguments.of(
                        new CreateUserVm(" ".repeat(4), VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                        "username", "Username can only contain letters, numbers, hyphens, and underscores"
                ),
                Arguments.of(
                        new CreateUserVm("a", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                        "username", "It must have minimum 4 and maximum 255 characters"
                ),
                Arguments.of(
                        new CreateUserVm("a".repeat(256), VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                        "username", "It must have minimum 4 and maximum 255 characters"
                ),
                Arguments.of(
                        new CreateUserVm("John Doe", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                        "username", "Username can only contain letters, numbers, hyphens, and underscores"
                ),
                Arguments.of(
                        new CreateUserVm("John:Doe", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                        "username", "Username can only contain letters, numbers, hyphens, and underscores"
                ),

                Arguments.of(
                        new CreateUserVm("user1", null, "The User", "user@example.com", "mock-token"),
                        "password", "Cannot be null"
                ),
                Arguments.of(
                        new CreateUserVm("user1", "Aa1Aa1", "The User", "user@example.com", "mock-token"),
                        "password", "It must have minimum 8 and maximum 255 characters"
                ),
                Arguments.of(
                        new CreateUserVm("user1", "Aa1".repeat(90), "The User", "user@example.com", "mock-token"),
                        "password", "It must have minimum 8 and maximum 255 characters"
                ),
                Arguments.of(
                        new CreateUserVm("user1", "a".repeat(8), "The User", "user@example.com", "mock-token"),
                        "password", "Password must have at least one uppercase, one lowercase letter and one number"
                ),

                Arguments.of(
                        new CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, null, "user@example.com", "mock-token"),
                        "name", "Cannot be null"
                ),
                Arguments.of(
                        new CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "a".repeat(3), "user@example.com", "mock-token"),
                        "name", "It must have minimum 4 and maximum 255 characters"
                ),
                Arguments.of(
                        new CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "a".repeat(256), "user@example.com", "mock-token"),
                        "name", "It must have minimum 4 and maximum 255 characters"
                ),

                Arguments.of(
                        new CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", null, "mock-token"),
                        "email", "Cannot be null"
                ),
                Arguments.of(
                        new CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "a", "mock-token"),
                        "email", "It must be a well-formed email address"
                ),

                Arguments.of(
                        new CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", null),
                        "recaptchaToken", "reCAPTCHA cannot be null or blank"
                ),
                Arguments.of(
                        new CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", ""),
                        "recaptchaToken", "reCAPTCHA cannot be null or blank"
                ),
                Arguments.of(
                        new CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "  "),
                        "recaptchaToken", "reCAPTCHA cannot be null or blank"
                )
        );
    }

    private ApiError performBadRequest(
            CreateUserVm createUserVm, MockMultipartFile imageFile) throws Exception {
        var jsonResponse = mockMvc.perform(createMultipartRequest(createUserVm, imageFile))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(jsonResponse, ApiError.class);
    }

    private static MockHttpServletRequestBuilder createMultipartRequest(
            CreateUserVm createUserVm, MockMultipartFile imageFile) {

        MockMultipartHttpServletRequestBuilder builder = multipart("/api/user");

        if (imageFile != null) {
            builder.file(imageFile);
        }

        return builder
                .param("username", createUserVm.username())
                .param("password", createUserVm.password())
                .param("email", createUserVm.email())
                .param("name", createUserVm.name())
                .param("recaptchaToken", createUserVm.recaptchaToken())
                .contentType(MediaType.MULTIPART_FORM_DATA); // not required, just for clarity
    }
}