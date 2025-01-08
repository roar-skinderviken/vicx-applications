package no.vicx.backend.user;

import no.vicx.backend.BaseWebMvcTest;
import no.vicx.backend.error.ApiError;
import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.vm.ChangePasswordVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.stream.Stream;

import static no.vicx.backend.testconfiguration.TestSecurityConfig.*;
import static no.vicx.database.user.VicxUser.VALID_PLAINTEXT_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PasswordController.class)
class PasswordControllerPatchTest extends BaseWebMvcTest {

    @MockitoBean
    UserService userService;

    @Test
    void changePassword_givenNoAuthHeader_expectUnauthorized() throws Exception {
        mockMvc.perform(createValidChangePasswordRequest("{}", false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_givenUserWithoutRequiredRole_expectForbidden() throws Exception {
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(
                createPrincipalInTest(Collections.emptyList()));

        mockMvc.perform(createValidChangePasswordRequest("{}", true))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{}"})
    void changePassword_givenEmptyBody_expectBadRequest(String content) throws Exception {
        mockMvc.perform(createValidChangePasswordRequest(content, true))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_givenInvalidCurrentPassword_expectBadRequest() throws Exception {
        when(userService.isValidPassword(anyString(),anyString())).thenReturn(false);

        var body = new ChangePasswordVm("~current-password~", VALID_PLAINTEXT_PASSWORD);

        var content = mockMvc.perform(createValidChangePasswordRequest(objectMapper.writeValueAsString(body), true))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var apiError = objectMapper.readValue(content, ApiError.class);
        assertEquals("Incorrect current password, please try again", apiError.validationErrors().get("currentPassword"));
    }

    @Test
    void changePassword_givenValidRequest_expectOk() throws Exception {
        when(userService.isValidPassword(anyString(),anyString())).thenReturn(true);

        var body = new ChangePasswordVm("~current-password~", VALID_PLAINTEXT_PASSWORD);

        mockMvc.perform(createValidChangePasswordRequest(objectMapper.writeValueAsString(body), true))
                .andExpect(status().isOk())
                .andExpect(content().string("Your password has been successfully updated."));

        verify(userService).updatePassword(body, "user1");
    }

    @ParameterizedTest
    @MethodSource("invalidRequestsSource")
    void patch_givenInvalidRequest_expectBadRequest(
            ChangePasswordVm body, String expectedErrorField, String expectedMessage) throws Exception {

        var contentAsString = mockMvc.perform(createValidChangePasswordRequest(objectMapper.writeValueAsString(body), true))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var apiError = objectMapper.readValue(contentAsString, ApiError.class);
        assertEquals(expectedMessage, apiError.validationErrors().get(expectedErrorField));
    }

    private static Stream<Arguments> invalidRequestsSource() {
        return Stream.of(
                Arguments.of(
                        new ChangePasswordVm(null, "Aa1Aa1Aa"),
                        "currentPassword", "Cannot be null"),
                Arguments.of(
                        new ChangePasswordVm("", "Aa1Aa1Aa"),
                        "currentPassword", "It must have minimum 4 and maximum 255 characters"),
                Arguments.of(
                        new ChangePasswordVm("Aa1", "Aa1Aa1Aa"),
                        "currentPassword", "It must have minimum 4 and maximum 255 characters"),
                Arguments.of(
                        new ChangePasswordVm("Aa1".repeat(90), "Aa1Aa1Aa"),
                        "currentPassword", "It must have minimum 4 and maximum 255 characters"),

                Arguments.of(
                        new ChangePasswordVm("~current-password~", null),
                        "password", "Cannot be null"),
                Arguments.of(
                        new ChangePasswordVm("~current-password~", "Aa1Aa1"),
                        "password", "It must have minimum 8 and maximum 255 characters"),
                Arguments.of(
                        new ChangePasswordVm("~current-password~", "Aa1".repeat(90)),
                        "password", "It must have minimum 8 and maximum 255 characters"),
                Arguments.of(
                        new ChangePasswordVm("~current-password~", "a".repeat(8)),
                        "password", "Password must have at least one uppercase, one lowercase letter and one number")
        );
    }

    private static MockHttpServletRequestBuilder createValidChangePasswordRequest(String content, boolean addAuth) {
        var requestBuilder =
                patch("/api/user/password")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON);

        if (addAuth) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST);
        }

        return requestBuilder;
    }
}