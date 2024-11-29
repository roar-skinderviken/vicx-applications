package no.vicx.backend.jwt.github;

import no.vicx.backend.jwt.github.vm.GitHubUserResponseVm;
import no.vicx.backend.jwt.github.vm.GitHubUserVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;

import static no.vicx.backend.jwt.JwtConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;


class GitHubJwtFromOpaqueProducerTest {

    @Mock
    GitHubUserFetcher userFetcher;

    @InjectMocks
    GitHubJwtFromOpaqueProducer sut;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void createJwt_givenValidToken_expectJwt() {
        // Arrange
        when(userFetcher.fetchUser(anyString())).thenReturn(
                new GitHubUserResponseVm(
                        new GitHubUserVm(
                                "12345",
                                "john-doe",
                                "John Doe",
                                "john.doe@example.com",
                                "https://example.com/avatar.jpg"
                        ),
                        "repo, user",
                        null,
                        "valid-token"
                )
        );

        // Act
        var jwt = sut.createJwt("valid-token");

        // Assert
        assertNotNull(jwt);
        assertEquals("john-doe", jwt.getClaim(CLAIM_SUB));
        assertEquals("John Doe", jwt.getClaim(CLAIM_NAME));
        assertEquals("john.doe@example.com", jwt.getClaim(CLAIM_EMAIL));
        assertEquals("https://example.com/avatar.jpg", jwt.getClaim(CLAIM_IMAGE));
        assertEquals("repo, user", jwt.getClaim(CLAIM_SCOPES));
        assertEquals(Collections.singletonList("ROLE_GITHUB_USER"), jwt.getClaim(CLAIM_ROLES));
    }

    @Test
    void createJwt_givenResponseWithoutEmailInUserResponse_expectJwtWithEmail() {
        // Arrange
        when(userFetcher.fetchUser(anyString())).thenReturn(
                new GitHubUserResponseVm(
                        new GitHubUserVm(
                                "12345",
                                "john-doe",
                                "John Doe",
                                null,
                                "https://example.com/avatar.jpg"
                        ),
                        "repo, user",
                        "john.doe@example.com",
                        "valid-token"
                )
        );

        // Act
        var jwt = sut.createJwt("valid-token");

        // Assert
        assertNotNull(jwt);
        assertEquals("john.doe@example.com", jwt.getClaim(CLAIM_EMAIL));
    }

    @Test
    void createJwt_givenResponseWithoutPrimaryEmail_expectJwtWithoutEmail() {
        // Arrange
        when(userFetcher.fetchUser(anyString())).thenReturn(
                new GitHubUserResponseVm(
                        new GitHubUserVm(
                                "12345",
                                "john-doe",
                                "John Doe",
                                null,
                                "https://example.com/avatar.jpg"
                        ),
                        "repo, user",
                        null,
                        "valid-token"
                )
        );

        // Act
        var jwt = sut.createJwt("valid-token");

        // Assert
        assertNotNull(jwt);
        assertNull(jwt.getClaim(CLAIM_EMAIL));
    }

    @Test
    void createJwt_givenWebClientResponseException_expectJwtException() {
        when(userFetcher.fetchUser(anyString())).thenThrow(WebClientResponseException.create(
                HttpStatus.UNAUTHORIZED.value(),  // HTTP status code
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), // Reason phrase
                null,  // Headers (optional)
                null,  // Body (optional)
                null   // Charset (optional)
        ));

        var exception = assertThrows(JwtException.class, () -> sut.createJwt("valid-token"));
        assertEquals("Invalid or expired GitHub access token", exception.getMessage());
    }

    @Test
    void createJwt_givenRuntimeException_expectJwtException() {
        when(userFetcher.fetchUser(anyString())).thenThrow(new RuntimeException());

        var exception = assertThrows(JwtException.class, () -> sut.createJwt("valid-token"));
        assertEquals("Error validating GitHub token", exception.getMessage());
    }
}