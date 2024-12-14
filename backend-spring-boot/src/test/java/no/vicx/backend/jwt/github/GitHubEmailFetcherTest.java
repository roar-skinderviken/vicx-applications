package no.vicx.backend.jwt.github;

import no.vicx.backend.config.RestClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static no.vicx.backend.jwt.github.GitHubEmailFetcher.EMAILS_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(GitHubEmailFetcher.class)
@Import(RestClientConfig.class)
class GitHubEmailFetcherTest {

    @Autowired
    GitHubEmailFetcher sut;

    @Autowired
    MockRestServiceServer mockServer;

    @Test
    void fetchEmail_givenResponseWithSinglePrimary_expectPrimary() {
        mockServer.expect(requestTo(EMAILS_URL))
                .andRespond(withSuccess("""
                        [
                          {
                            "email": "user@hotmail.com",
                            "primary": false,
                            "verified": true,
                            "visibility": "private"
                          },
                          {
                            "email": "user@example.com",
                            "primary": true,
                            "verified": true,
                            "visibility": "private"
                          }
                        ]""", MediaType.APPLICATION_JSON));

        assertEquals("user@example.com", sut.fetchEmail("some-token"));
    }

    @Test
    void fetchEmail_givenResponseBodyWithEmptyArray_expectNull() {
        mockServer.expect(requestTo(EMAILS_URL))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertNull(sut.fetchEmail("some-token"));
    }

    @Test
    void fetchEmail_givenEmptyResponseBody_expectNull() {
        mockServer.expect(requestTo(EMAILS_URL))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        assertNull(sut.fetchEmail("some-token"));
    }
}