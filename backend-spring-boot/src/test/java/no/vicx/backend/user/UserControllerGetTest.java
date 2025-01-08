package no.vicx.backend.user;

import no.vicx.backend.BaseWebMvcTest;
import no.vicx.backend.error.NotFoundException;
import no.vicx.backend.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;

import static no.vicx.backend.testconfiguration.TestSecurityConfig.*;
import static no.vicx.backend.user.UserTestUtils.createValidVicxUser;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerGetTest extends BaseWebMvcTest {

    @MockitoBean
    UserService userService;

    @Test
    void getUser_givenNoAuthHeader_expectUnauthorized() throws Exception {
        mockMvc
                .perform(get("/api/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUser_givenUserWithoutRequiredRole_expectForbidden() throws Exception {
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(
                createPrincipalInTest(Collections.emptyList()));

        mockMvc
                .perform(get("/api/user").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUser_givenAuthHeader_expectOk() throws Exception {
        var validUser = createValidVicxUser();
        when(userService.getUserByUserName("user1")).thenReturn(validUser);

        mockMvc
                .perform(get("/api/user").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST))
                .andExpect(status().isOk());

        verify(userService).getUserByUserName(anyString());
    }

    @Test
    void getUser_givenMissingUser_expectNotFound() throws Exception {
        when(userService.getUserByUserName("user1")).thenThrow(new NotFoundException("User user1 not found"));

        mockMvc
                .perform(get("/api/user").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST))
                .andExpect(status().isNotFound());
    }
}