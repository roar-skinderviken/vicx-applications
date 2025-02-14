package no.vicx.backend.jwt.github;

import no.vicx.backend.jwt.github.vm.GitHubUserResponseVm;
import no.vicx.backend.jwt.github.vm.GitHubUserVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubFromOpaqueProducerTest {

    @Mock
    GitHubUserFetcher userFetcher;

    @InjectMocks
    GitHubFromOpaqueProducer sut;

    @Test
    void createPrincipal_givenValidToken_expectPrincipal() {
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
                        "valid-token"));

        var principal = sut.createPrincipal("valid-token");

        assertNotNull(principal);
        assertEquals("john-doe", principal.getName());
        assertEquals(
                Collections.singletonList("ROLE_GITHUB_USER"),
                principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

    @Test
    void createPrincipal_givenWebClientResponseException_expectBadOpaqueTokenException() {
        when(userFetcher.fetchUser(anyString())).thenThrow(HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED,
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                null,
                null,
                null));

        var exception = assertThrows(
                BadOpaqueTokenException.class, () -> sut.createPrincipal("valid-token"));

        assertEquals("Invalid or expired GitHub access token", exception.getMessage());
    }

    @Test
    void createPrincipal_givenRuntimeException_expectBadOpaqueTokenException() {
        when(userFetcher.fetchUser(anyString())).thenThrow(new RuntimeException());

        var exception = assertThrows(
                BadOpaqueTokenException.class, () -> sut.createPrincipal("valid-token"));

        assertEquals("Error validating GitHub token", exception.getMessage());
    }
}