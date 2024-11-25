package no.vicx.backend.jwt.github;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static no.vicx.backend.jwt.github.GitHubUserFetcher.HEADER_SCOPES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GitHubUserFetcherTest {
    @Mock
    ExchangeFunction exchangeFunction;

    @Mock
    GitHubEmailFetcher emailFetcher;

    GitHubUserFetcher sut;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        sut = new GitHubUserFetcher(webClient, emailFetcher);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void fetchUser_givenResponseWithAllFieldsSet_expectUser() {
        var userResponse = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HEADER_SCOPES, "repo, user")
                .body("""
                        {
                            "id": "12345",
                            "login": "john-doe",
                            "name": "John Doe",
                            "email": "john.doe@example.com",
                            "avatar_url": "https://example.com/avatar.jpg"
                        }""")
                .build();


        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(userResponse));

        var userResp = sut.fetchUser("some-token");
        var user = userResp.user();

        assertNotNull(userResponse);
        assertEquals("john-doe", user.login());
        assertEquals("John Doe", user.name());
        assertEquals("john.doe@example.com", user.email());
        assertEquals("https://example.com/avatar.jpg", user.avatarUrl());
        assertEquals("repo, user", userResp.grantedScopes());
        assertNull(userResp.additionalEmailAddress());
    }

    @Test
    void fetchUser_givenResponseWithoutEmail_expectEmailFetcherToBeInvoked() {
        var userResponse = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HEADER_SCOPES, "repo, user")
                .body("""
                        {
                            "id": "12345",
                            "login": "john-doe",
                            "name": "John Doe",
                            "avatar_url": "https://example.com/avatar.jpg"
                        }""")
                .build();

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(userResponse));

        when(emailFetcher.fetchEmail(anyString())).thenReturn("email@example.com");

        var userResp = sut.fetchUser("some-token");
        assertEquals("email@example.com", userResp.additionalEmailAddress());

        verify(emailFetcher).fetchEmail(anyString());
    }

    @Test
    void fetchUser_givenEmptyResponse_expectException() {
        var userResponse = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HEADER_SCOPES, "repo, user")
                .body("{}")
                .build();

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(userResponse));

        assertThrows(IllegalStateException.class, () -> sut.fetchUser("some-token"));
    }

    @Test
    void fetchUser_givenMissingResponseBody_expectException() {
        // Arrange
        var clientResponse = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(clientResponse));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> sut.fetchUser("some-token"));
    }

    @Test
    void fetchUser_givenUnauthorizedResponse_expectException() {
        // Arrange
        var clientResponse = ClientResponse.create(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(clientResponse));

        // Act & Assert
        assertThrows(WebClientResponseException.class, () -> sut.fetchUser("invalid-token"));
    }

    @Test
    void createJwt_givenInternalServerError_expectException() {
        // Arrange
        when(exchangeFunction.exchange(any())).thenThrow(new RuntimeException("Internal Server Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> sut.fetchUser("some-token"));
    }
}