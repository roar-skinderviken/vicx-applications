package no.vicx.backend.jwt.github;

import no.vicx.backend.config.RestClientConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;

import static no.vicx.backend.jwt.github.GitHubUserFetcher.HEADER_SCOPES;
import static no.vicx.backend.jwt.github.GitHubUserFetcher.USER_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(GitHubUserFetcher.class)
@Import({RestClientConfig.class, GitHubEmailFetcher.class})
class GitHubUserFetcherTest {

    @MockitoBean
    GitHubEmailFetcher emailFetcher;

    @Autowired
    GitHubUserFetcher sut;

    @Autowired
    MockRestServiceServer mockServer;

    @Test
    void fetchUser_givenResponseWithAllFieldsSet_expectUser() {
        mockServer.expect(requestTo(USER_URL))
                .andRespond(withSuccess("""
                        {
                            "id": "12345",
                            "login": "john-doe",
                            "name": "John Doe",
                            "email": "john.doe@example.com",
                            "avatar_url": "https://example.com/avatar.jpg"
                        }""", MediaType.APPLICATION_JSON)
                        .header(HEADER_SCOPES, "repo, user"));

        var userResponseVm = sut.fetchUser("some-token");
        var user = userResponseVm.user();

        assertEquals("john-doe", user.login());
        assertEquals("John Doe", user.name());
        assertEquals("john.doe@example.com", user.email());
        assertEquals("https://example.com/avatar.jpg", user.avatarUrl());
        assertEquals("repo, user", userResponseVm.grantedScopes());
        assertNull(userResponseVm.additionalEmailAddress());
    }

    @Test
    void fetchUser_givenResponseWithoutEmail_expectEmailFetcherToBeInvoked() {
        mockServer.expect(requestTo(USER_URL))
                .andRespond(withSuccess("""
                        {
                            "id": "12345",
                            "login": "john-doe",
                            "name": "John Doe",
                            "avatar_url": "https://example.com/avatar.jpg"
                        }""", MediaType.APPLICATION_JSON)
                        .header(HEADER_SCOPES, "repo, user"));
        
        when(emailFetcher.fetchEmail(anyString())).thenReturn("email@example.com");

        var userResponseVm = sut.fetchUser("some-token");
        assertEquals("email@example.com", userResponseVm.additionalEmailAddress());

        verify(emailFetcher).fetchEmail(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{}"})
    void fetchUser_givenEmptyResponseBody_expectException(String responseBody) {
        mockServer.expect(requestTo(USER_URL))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        assertThrows(IllegalStateException.class, () -> sut.fetchUser("some-token"));
    }

    @Test
    void fetchUser_givenUnauthorizedResponse_expectException() {
        mockServer.expect(requestTo(USER_URL))
                .andRespond(withStatus(HttpStatusCode.valueOf(401)));

        assertThrows(HttpClientErrorException.class, () -> sut.fetchUser("invalid-token"));
    }
}