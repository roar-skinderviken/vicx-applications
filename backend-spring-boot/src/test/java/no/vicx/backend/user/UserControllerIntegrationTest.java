package no.vicx.backend.user;

import no.vicx.backend.error.ApiError;
import no.vicx.backend.testconfiguration.TestSecurityConfig;
import no.vicx.backend.user.vm.UserVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import static no.vicx.backend.user.UserTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
class UserControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @MockBean
    UserService userService;

    @Test
    void getUser_notAuthenticated_expectUnauthorized() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/user/user1", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getUser_authenticated_expectOk() {
        when(userService.getUserByUserName("user1")).thenReturn(VALID_VICX_USER);

        var headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer token");

        var httpEntity = new HttpEntity<>(headers);

        var response = restTemplate.exchange(
                "/api/user/user1",
                HttpMethod.GET,
                httpEntity,
                UserVm.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertUserVm(VALID_VICX_USER, response.getBody());
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
        when(userService.updateUser(any())).thenReturn(VALID_VICX_USER);

        var headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer token");

        var response = restTemplate.exchange(
                "/api/user",
                HttpMethod.PUT,
                new HttpEntity<>(VALID_USER_VM, headers),
                UserVm.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertUserVm(VALID_VICX_USER, response.getBody());
    }

    @Test
    void putUser_givenUsernameForOtherUser_expectForbidden() {
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer token");

        var user = createUserVm(
                "user2", "P4ssword", "user@example.com", "The User");

        var response = restTemplate.exchange(
                "/api/user",
                HttpMethod.PUT,
                new HttpEntity<>(user, headers),
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#invalidUserVmProvider")
    void putUser_givenInvalidUser_expectBadRequest(
            UserVm userVm, String fieldName, String expectedMessage
    ) {
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer token");

        var response = restTemplate.exchange(
                "/api/user",
                HttpMethod.PUT,
                new HttpEntity<>(userVm, headers),
                ApiError.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        var apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(expectedMessage, apiError.validationErrors().get(fieldName));
    }
}