package no.vicx.backend.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.vicx.backend.config.SecurityConfig;
import no.vicx.backend.error.ApiError;
import no.vicx.backend.user.vm.UserVm;
import no.vicx.database.user.UserRepository;
import no.vicx.database.user.VicxUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static no.vicx.backend.user.UserTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @ParameterizedTest
    @MethodSource("validUserVmProvider")
    void postUser_givenValidUser_expectCreated(UserVm userVm) throws Exception {
        var validVicxUser = userVm.toNewVicxUser();
        validVicxUser.setId(42L);

        when(userService.createUser(any())).thenReturn(validVicxUser);

        mockMvc.perform(createRequestBuilder(userVm))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/user/" + validVicxUser.getId()))
                .andExpect(content().string("User created successfully."));

        var captor = ArgumentCaptor.forClass(VicxUser.class);
        verify(userService, times(1)).createUser(captor.capture());

        var capturedUser = captor.getValue();
        assertEquals(validVicxUser.getUsername(), capturedUser.getUsername());
    }

    @ParameterizedTest
    @MethodSource("no.vicx.backend.user.UserTestUtils#invalidUserVmProvider")
    void postUser_givenInvalidUser_expectBadRequest(
            UserVm userVm, String fieldName, String expectedMessage) throws Exception {

        var apiError = performBadRequest(userVm);
        assertEquals(expectedMessage, apiError.validationErrors().get(fieldName));
    }

    @Test
    void postUser_givenDuplicateUsername_expectBadRequest() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(VALID_VICX_USER));

        var apiError = performBadRequest(VALID_USER_VM);

        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.status());
        assertEquals("validation error", apiError.message());
        assertEquals("This name is in use", apiError.validationErrors().get("username"));
    }

    static Stream<Arguments> validUserVmProvider() throws IOException {
        return Stream.of(
                Arguments.of(createUserVm(readFileToBase64("profile.png"))),
                Arguments.of(createUserVm(null)),
                Arguments.of(createUserVm(""))
        );
    }

    MockHttpServletRequestBuilder createRequestBuilder(UserVm userVm) throws JsonProcessingException {
        return post("/api/user")
                .content(objectMapper.writeValueAsString(userVm))
                .contentType(MediaType.APPLICATION_JSON);
    }

    ApiError performBadRequest(UserVm userVm) throws Exception {
        var result = mockMvc.perform(createRequestBuilder(userVm))
                .andExpect(status().isBadRequest())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
    }
}