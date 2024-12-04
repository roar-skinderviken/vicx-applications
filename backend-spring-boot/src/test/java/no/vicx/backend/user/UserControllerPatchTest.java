package no.vicx.backend.user;

import no.vicx.backend.BaseWebMvcTest;
import no.vicx.backend.error.ApiError;
import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.vm.UserPatchVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.stream.Stream;

import static no.vicx.backend.testconfiguration.TestSecurityConfig.AUTH_HEADER_IN_TEST;
import static no.vicx.backend.testconfiguration.TestSecurityConfig.createJwtInTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerPatchTest extends BaseWebMvcTest {

    @MockitoBean
    UserService userService;

    @Test
    void patchName_givenNoAuthHeader_expectUnauthorized() throws Exception {
        mockMvc.perform(createPatchRequest("{}", false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchName_givenUserWithoutRequiredRole_expectForbidden() throws Exception {
        when(jwtDecoder.decode(anyString()))
                .thenReturn(createJwtInTest(Collections.emptyList()));

        mockMvc.perform(createPatchRequest("{}", true))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchName_givenEmptyBody_expectBadRequest() throws Exception {
        mockMvc.perform(createPatchRequest("", true))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("validUserPatchRequestsSource")
    void patch_givenValidRequest_expectOk(UserPatchVm body) throws Exception {
        mockMvc.perform(createPatchRequest(objectMapper.writeValueAsString(body), true))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully."));

        verify(userService).updateUser(body, "user1");
    }

    @ParameterizedTest
    @MethodSource("invalidRequestsSource")
    void patch_givenInvalidRequest_expectBadRequest(
            UserPatchVm body, String expectedErrorField, String expectedMessage) throws Exception {

        var contentAsString = mockMvc.perform(createPatchRequest(objectMapper.writeValueAsString(body), true))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var apiError = objectMapper.readValue(contentAsString, ApiError.class);
        assertEquals(expectedMessage, apiError.validationErrors().get(expectedErrorField));
    }

    private static Stream<Arguments> validUserPatchRequestsSource() {
        return Stream.of(
                Arguments.of(new UserPatchVm("~name~", "foo@bar.com")),
                Arguments.of(new UserPatchVm("~name~", null)),
                Arguments.of(new UserPatchVm(null, "foo@bar.com"))
        );
    }

    private static Stream<Arguments> invalidRequestsSource() {
        return Stream.of(
                Arguments.of(
                        new UserPatchVm(null, null),
                        "patchRequestBody", "At least one field must be provided"),

                Arguments.of(
                        new UserPatchVm("a".repeat(3), null),
                        "name", "It must have minimum 4 and maximum 255 characters"),
                Arguments.of(
                        new UserPatchVm("a".repeat(256), null),
                        "name", "It must have minimum 4 and maximum 255 characters"),

                Arguments.of(
                        new UserPatchVm(null, "a"),
                        "email", "It must be a well-formed email address")
        );
    }

    private static MockHttpServletRequestBuilder createPatchRequest(String content, boolean addAuth) {
        var requestBuilder = patch("/api/user")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON);

        if (addAuth) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST);
        }

        return requestBuilder;
    }
}