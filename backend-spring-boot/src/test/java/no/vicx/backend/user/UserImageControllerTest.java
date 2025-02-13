package no.vicx.backend.user;

import no.vicx.backend.BaseWebMvcTest;
import no.vicx.backend.error.ApiError;
import no.vicx.backend.user.service.UserImageService;
import no.vicx.database.user.UserImage;
import no.vicx.database.user.UserImageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.Optional;

import static no.vicx.backend.testconfiguration.SecurityTestUtils.*;
import static no.vicx.backend.user.UserTestUtils.createMultipartFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserImageController.class)
class UserImageControllerTest extends BaseWebMvcTest {

    @MockitoBean
    UserImageService userImageService;

    @MockitoBean
    UserImageRepository userImageRepository;

    // START post

    @Test
    void postUserImage_givenNoAuthHeader_expectUnauthorized() throws Exception {
        var file = createMultipartFile("test-png.png", MediaType.IMAGE_PNG_VALUE);

        mockMvc
                .perform(createMultipartRequest(file, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postUserImage_givenUserWithoutRequiredRole_expectForbidden() throws Exception {
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(
                createPrincipalInTest(Collections.emptyList()));

        var file = createMultipartFile("test-png.png", MediaType.IMAGE_PNG_VALUE);

        mockMvc
                .perform(createMultipartRequest(file, true))
                .andExpect(status().isForbidden());
    }

    @Test
    void postUserImage_givenAuthHeader_expectOk() throws Exception {
        var file = createMultipartFile("test-png.png", MediaType.IMAGE_PNG_VALUE);

        mockMvc
                .perform(createMultipartRequest(file, true))
                .andExpect(status().isCreated());

        verify(userImageService).addOrReplaceUserImage(any(), anyString());
    }

    @Test
    void postUserImage_givenInvalidMime_expectBadRequest() throws Exception {
        var file = createMultipartFile("test-gif.gif", MediaType.IMAGE_GIF_VALUE);

        var content = mockMvc
                .perform(createMultipartRequest(file, true))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var apiError = objectMapper.readValue(content, ApiError.class);

        assertEquals("Only PNG and JPG files are allowed", apiError.validationErrors().get("image"));
    }

    @Test
    void postUserImage_givenTooLargeFile_expectBadRequest() throws Exception {
        var file = createMultipartFile("too-large.png", MediaType.IMAGE_PNG_VALUE);

        var content = mockMvc
                .perform(createMultipartRequest(file, true))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var apiError = objectMapper.readValue(content, ApiError.class);

        assertEquals(
                "File size exceeds the maximum allowed size of 51200 bytes",
                apiError.validationErrors().get("image"));
    }

    @Test
    void postUserImage_givenNullFile_expectBadRequest() throws Exception {
        var content = mockMvc
                .perform(createMultipartRequest(null, true))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var apiError = objectMapper.readValue(content, ApiError.class);

        assertEquals(
                "Cannot be null",
                apiError.validationErrors().get("image"));
    }

    // START get

    @Test
    void getUserImage_withoutCredentials_expectUnauthorized() throws Exception {
        mockMvc
                .perform(get("/api/user/image"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserImage_imageExistsInDatabase_expectOk() throws Exception {
        var userImage = new UserImage(new byte[]{1, 2, 3}, MediaType.IMAGE_JPEG_VALUE);
        when(userImageRepository.findByUserUsername(anyString())).thenReturn(Optional.of(userImage));

        mockMvc
                .perform(get("/api/user/image").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST))
                .andExpect(status().isOk());

        verify(userImageRepository).findByUserUsername("user1");
    }

    @Test
    void getUserImage_imageDoesNotExistInDatabase_expectNotFound() throws Exception {
        mockMvc
                .perform(get("/api/user/image").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST))
                .andExpect(status().isNotFound());

        verify(userImageRepository).findByUserUsername("user1");
    }

    // START delete

    @Test
    void delete_givenNoAuthHeader_expectUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/user/image"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_givenAuthHeader_expectNoContent() throws Exception {
        mockMvc.perform(delete("/api/user/image")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST))
                .andExpect(status().isNoContent());

        verify(userImageService).deleteUserImage(anyString());
    }

    // START utils

    private static MockHttpServletRequestBuilder createMultipartRequest(
            MockMultipartFile imageFile, boolean addAuth) {

        var builder = imageFile != null
                ? multipart("/api/user/image").file(imageFile)
                : multipart("/api/user/image");

        builder.contentType(MediaType.MULTIPART_FORM_DATA);

        if (addAuth) {
            builder.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST);
        }

        return builder;
    }
}