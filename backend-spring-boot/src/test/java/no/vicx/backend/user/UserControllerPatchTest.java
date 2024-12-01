package no.vicx.backend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.vicx.backend.config.SecurityConfig;
import no.vicx.backend.error.ApiError;
import no.vicx.backend.testconfiguration.TestSecurityConfig;
import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.vm.UserPatchRequestVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class UserControllerPatchTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @Test
    void patchName_givenUnauthorizedRequest_expectUnauthorized() throws Exception {
        mockMvc.perform(createPatchRequest("{}", false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchName_givenEmptyBody_expectBadRequest() throws Exception {
        mockMvc.perform(createPatchRequest("", true))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("validUserPatchRequestsSource")
    void patch_givenValidRequest_expectOk(UserPatchRequestVm body) throws Exception {
        mockMvc.perform(createPatchRequest(objectMapper.writeValueAsString(body), true))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully."));

        verify(userService).updateUser(body, "user1");
    }

    @ParameterizedTest
    @MethodSource("invalidUserPatchRequestsSource")
    void patch_givenInvalidRequest_expectBadRequest(
            UserPatchRequestVm body, String expectedErrorField, String expectedMessage) throws Exception {

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
                Arguments.of(new UserPatchRequestVm("P4ssword", "~name~", "foo@bar.com")),
                Arguments.of(new UserPatchRequestVm("P4ssword", null, null)),
                Arguments.of(new UserPatchRequestVm(null, "~name~", null)),
                Arguments.of(new UserPatchRequestVm(null, null, "foo@bar.com"))
        );
    }

    private static Stream<Arguments> invalidUserPatchRequestsSource() {
        return Stream.of(
                Arguments.of(
                        new UserPatchRequestVm(null, null, null),
                        "patchRequestBody", "At least one field must be provided"),

                Arguments.of(
                        new UserPatchRequestVm("Aa1Aa1A", null, null),
                        "password", "It must have minimum 8 and maximum 255 characters"),
                Arguments.of(
                        new UserPatchRequestVm("Aa1".repeat(90), null, null),
                        "password", "It must have minimum 8 and maximum 255 characters"),
                Arguments.of(
                        new UserPatchRequestVm("a".repeat(8), null, null),
                        "password", "Password must have at least one uppercase, one lowercase letter and one number"),

                Arguments.of(
                        new UserPatchRequestVm(null, "a".repeat(3), null),
                        "name", "It must have minimum 4 and maximum 255 characters"),
                Arguments.of(
                        new UserPatchRequestVm(null, "a".repeat(256), null),
                        "name", "It must have minimum 4 and maximum 255 characters"),

                Arguments.of(
                        new UserPatchRequestVm(null, null, "a"),
                        "email", "It must be a well-formed email address")
        );
    }

    private static MockHttpServletRequestBuilder createPatchRequest(String content, boolean addAuth) {
        var requestBuilder =
                patch("/api/user")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON);

        if (addAuth) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer some-token");
        }

        return requestBuilder;
    }
}