package no.vicx.backend.user;

import no.vicx.backend.config.SecurityConfig;
import no.vicx.backend.error.NotFoundException;
import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.vm.UserVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

import static no.vicx.backend.testconfiguration.SecurityTestUtils.AUTH_HEADER_IN_TEST;
import static no.vicx.backend.testconfiguration.SecurityTestUtils.createPrincipalInTest;
import static no.vicx.backend.user.UserTestUtils.createValidVicxUser;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerGetWebTestClientTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    UserService userService;

    @MockitoBean
    protected OpaqueTokenIntrospector opaqueTokenIntrospector;

    @BeforeEach
    void setUp() {
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(
                createPrincipalInTest(Collections.singletonList("ROLE_USER")));
    }

    @Test
    void getUser_givenNoAuthHeader_expectUnauthorized() {
        createGetSpec(false)
                .expectStatus().isUnauthorized();
    }

    @Test
    void getUser_givenUserWithoutRequiredRole_expectForbidden() {
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(
                createPrincipalInTest(Collections.emptyList()));

        createGetSpec(true)
                .expectStatus().isForbidden();
    }

    @Test
    void getUser_givenMissingUser_expectNotFound() {
        when(userService.getUserByUserName("user1")).thenThrow(new NotFoundException("User user1 not found"));

        createGetSpec(true)
                .expectStatus().isNotFound();
    }

    @Test
    void getUser_givenAuthHeader_expectOk() {
        var validUser = createValidVicxUser();
        when(userService.getUserByUserName("user1")).thenReturn(validUser);

        createGetSpec(true)
                .expectStatus().isOk()
                .expectBody(UserVm.class).isEqualTo(UserVm.fromVicxUser(validUser));

        verify(userService).getUserByUserName(anyString());
    }

    private WebTestClient.ResponseSpec createGetSpec(boolean addAuthHeader) {
        var spec = webTestClient
                .get()
                .uri("/api/user");

        if (addAuthHeader) {
            spec.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST);
        }

        return spec.exchange();
    }
}