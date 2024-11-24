package no.vicx.backend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.vicx.backend.config.SecurityConfig;
import no.vicx.backend.error.ApiError;
import no.vicx.backend.user.service.RecaptchaService;
import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.vm.UserVm;
import no.vicx.database.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static no.vicx.backend.user.UserController.USER_CREATED_BODY_TEXT;
import static no.vicx.backend.user.UserTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RecaptchaService recaptchaService;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        when(recaptchaService.verifyToken(any())).thenReturn(true);
    }

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#mockMultipartFileProvider")
    void postUser_givenValidUser_expectCreated(MockMultipartFile imageFile) throws Exception {
        var validVicxUser = VALID_USER_VM.toNewVicxUser();
        validVicxUser.setId(42L);

        when(userService.createUser(any(), any())).thenReturn(validVicxUser);

        mockMvc.perform(createMultipartRequest(VALID_USER_VM, imageFile))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/user/" + validVicxUser.getUsername()))
                .andExpect(content().string(USER_CREATED_BODY_TEXT));

        var userCaptor = ArgumentCaptor.forClass(UserVm.class);
        var imageCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(userService, times(1)).createUser(userCaptor.capture(), imageCaptor.capture());

        var capturedUser = userCaptor.getValue();
        assertEquals(validVicxUser.getUsername(), capturedUser.username());

        var capturedImage = imageCaptor.getValue();
        if (capturedImage != null) {
            assertEquals(imageFile.getOriginalFilename(), capturedImage.getOriginalFilename());
        }

        // make sure RecaptchaThenUniqueUsername validator is executed just once
        verify(recaptchaService, times(1)).verifyToken(anyString());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#invalidUserVmProvider")
    void postUser_givenInvalidUser_expectBadRequest(
            UserVm userVm, String fieldName, String expectedMessage) throws Exception {

        var apiError = performBadRequest(userVm, null);
        assertEquals(expectedMessage, apiError.validationErrors().get(fieldName));
    }

    @Test
    void postUser_GivenInvalidReCaptcha_expectBadRequest() throws Exception {
        when(recaptchaService.verifyToken(any())).thenReturn(false);

        var apiError = performBadRequest(VALID_USER_VM, null);

        assertEquals("Invalid reCAPTCHA, please try again", apiError.validationErrors().get("recaptchaToken"));

        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void postUser_givenDuplicateUsername_expectBadRequest() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(VALID_USER_VM.toNewVicxUser()));

        var apiError = performBadRequest(VALID_USER_VM, null);

        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.status());
        assertEquals("validation error", apiError.message());
        assertEquals("This name is in use", apiError.validationErrors().get("username"));

        verify(recaptchaService, times(1)).verifyToken(anyString());
    }

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#invalidImageProvider")
    void postUser_givenInvalidImage_expectBadRequest(
            MockMultipartFile imageFile, String fieldName, String expectedMessage) throws Exception {

        var apiError = performBadRequest(VALID_USER_VM, imageFile);
        assertEquals(expectedMessage, apiError.validationErrors().get(fieldName));
    }

    MockHttpServletRequestBuilder createMultipartRequest(
            UserVm userVm, MockMultipartFile imageFile) {

        var builder = multipart("/api/user");

        if (imageFile != null) {
            builder.file(imageFile);
        }

        return builder
                .param("username", userVm.username())
                .param("password", userVm.password())
                .param("email", userVm.email())
                .param("name", userVm.name())
                .param("recaptchaToken", userVm.recaptchaToken())
                .contentType(MediaType.MULTIPART_FORM_DATA); // not required, just for clarity
    }

    ApiError performBadRequest(
            UserVm userVm, MockMultipartFile imageFile) throws Exception {
        var jsonResponse = mockMvc.perform(createMultipartRequest(userVm, imageFile))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(jsonResponse, ApiError.class);
    }
}