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

import static no.vicx.backend.user.UserTestUtils.createValidUser;
import static no.vicx.backend.user.UserTestUtils.createValidUserVm;
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
        var validUserVm = createValidUserVm();
        var validVicxUser = validUserVm.toNewVicxUser();
        validVicxUser.setId(42L);

        when(userService.createUser(any(), any())).thenReturn(validVicxUser);

        mockMvc.perform(createMultipartRequest(validUserVm, imageFile))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/user/" + validVicxUser.getId()))
                .andExpect(content().string("User created successfully."));

        var userCaptor = ArgumentCaptor.forClass(UserVm.class);
        var imageCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(userService, times(1)).createUser(userCaptor.capture(), imageCaptor.capture());

        var capturedUser = userCaptor.getValue();
        assertEquals(validVicxUser.getUsername(), capturedUser.username());

        var capturedImage = imageCaptor.getValue();
        if (capturedImage != null) {
            assertEquals(imageFile.getOriginalFilename(), capturedImage.getOriginalFilename());
        }
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

        var apiError = performBadRequest(createValidUserVm(), null);

        assertEquals("Invalid reCAPTCHA, please try again", apiError.validationErrors().get("recaptchaToken"));

        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void postUser_givenDuplicateUsername_expectBadRequest() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(createValidUser()));

        var apiError = performBadRequest(createValidUserVm(), null);

        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.status());
        assertEquals("validation error", apiError.message());
        assertEquals("This name is in use", apiError.validationErrors().get("username"));

        verify(recaptchaService, times(1)).verifyToken(anyString());
    }

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#invalidImageProvider")
    void postUser_givenInvalidImage_expectBadRequest(
            MockMultipartFile imageFile, String fieldName, String expectedMessage) throws Exception {

        var apiError = performBadRequest(createValidUserVm(), imageFile);
        assertEquals(expectedMessage, apiError.validationErrors().get(fieldName));
    }

    MockHttpServletRequestBuilder createMultipartRequest(
            UserVm userVm, MockMultipartFile imageFile) {

        var builder = multipart("/api/user");

        if (imageFile != null) {
            builder.file(imageFile);
        }

        builder
                .param("username", userVm.username())
                .param("password", userVm.password())
                .param("email", userVm.email())
                .param("name", userVm.name())
                .param("recaptchaToken", userVm.recaptchaToken());

        return builder.contentType(MediaType.MULTIPART_FORM_DATA);
    }

    ApiError performBadRequest(
            UserVm userVm, MockMultipartFile imageFile) throws Exception {
        var result = mockMvc.perform(createMultipartRequest(userVm, imageFile))
                .andExpect(status().isBadRequest())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
    }
}