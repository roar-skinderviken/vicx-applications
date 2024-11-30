package no.vicx.backend.user;

import no.vicx.backend.error.ApiError;
import no.vicx.backend.testconfiguration.TestSecurityConfig;
import no.vicx.backend.user.service.RecaptchaService;
import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.vm.UserVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static no.vicx.backend.jwt.JwtConstants.BEARER_PREFIX;
import static no.vicx.backend.user.UserTestUtils.VALID_USER_VM;
import static no.vicx.backend.user.UserTestUtils.assertUserVm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
class UserControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @MockitoBean
    UserService userService;

    @MockitoBean
    RecaptchaService recaptchaService;

    @BeforeEach
    void setUp() {
        when(recaptchaService.verifyToken(any())).thenReturn(true);
    }

    @Test
    void getUser_notAuthenticated_expectUnauthorized() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/user/user1", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getUser_authenticated_expectOk() {
        var validUser = VALID_USER_VM.toNewVicxUser();
        when(userService.getUserByUserName("user1")).thenReturn(validUser);

        var httpEntity = new HttpEntity<>(
                createHttpHeaders(false, MediaType.APPLICATION_JSON_VALUE));

        var response = restTemplate.exchange(
                "/api/user/user1",
                HttpMethod.GET,
                httpEntity,
                UserVm.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertUserVm(validUser, response.getBody());
    }

    @Test
    void putUser_notAuthenticated_expectUnauthorized() {
        var response = restTemplate.exchange(
                "/api/user",
                HttpMethod.PUT,
                new HttpEntity<>(VALID_USER_VM),
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void putUser_authenticated_expectOk() {
        var validUser = VALID_USER_VM.toNewVicxUser();

        when(userService.updateUser(any())).thenReturn(validUser);

        var response = restTemplate.exchange(
                "/api/user",
                HttpMethod.PUT,
                new HttpEntity<>(
                        VALID_USER_VM,
                        createHttpHeaders(true, MediaType.APPLICATION_JSON_VALUE)),
                UserVm.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertUserVm(validUser, response.getBody());
    }

    @Test
    void putUser_givenUsernameForOtherUser_expectForbidden() {
        var user = new UserVm(
                "user2", "P4ssword", "The User",
                "user@example.com", "mock-token");

        var response = restTemplate.exchange(
                "/api/user",
                HttpMethod.PUT,
                new HttpEntity<>(
                        user,
                        createHttpHeaders(true, null)),
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#invalidUserVmProvider")
    void putUser_givenInvalidUser_expectBadRequest(
            UserVm userVm, String fieldName, String expectedMessage
    ) {
        var response = restTemplate.exchange(
                "/api/user",
                HttpMethod.PUT,
                new HttpEntity<>(
                        userVm,
                        createHttpHeaders(true, null)),
                ApiError.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        var apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(expectedMessage, apiError.validationErrors().get(fieldName));
    }

    HttpHeaders createHttpHeaders(
            boolean includeContentType,
            String acceptType
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + "token");

        if (includeContentType) headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (acceptType != null) headers.set(HttpHeaders.CONTENT_TYPE, acceptType);

        return headers;
    }
}