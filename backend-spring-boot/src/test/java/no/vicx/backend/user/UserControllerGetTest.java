package no.vicx.backend.user;

import no.vicx.backend.testconfiguration.TestSecurityConfig;
import no.vicx.backend.user.service.RecaptchaService;
import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.vm.UserVm;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static no.vicx.backend.jwt.JwtConstants.BEARER_PREFIX;
import static no.vicx.backend.user.UserTestUtils.createValidVicxUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
class UserControllerGetTest {

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
        var validUser = createValidVicxUser();
        when(userService.getUserByUserName("user1")).thenReturn(validUser);

        var httpEntity = new HttpEntity<>(createHttpHeaders());

        var response = restTemplate.exchange(
                "/api/user/user1",
                HttpMethod.GET,
                httpEntity,
                UserVm.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertUserVm(validUser, response.getBody());
    }

    HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + "token");

        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return headers;
    }

    static void assertUserVm(VicxUser user, UserVm userVm) {
        assertNotNull(userVm);
        assertNull(userVm.password());
        assertEquals(user.getUsername(), userVm.username());
        assertEquals(user.getEmail(), userVm.email());
        assertEquals(user.getName(), userVm.name());
    }
}