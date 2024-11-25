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
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GitHubEmailFetcherTest {

    @Mock
    ExchangeFunction exchangeFunction;

    GitHubEmailFetcher sut;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        sut = new GitHubEmailFetcher(webClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void fetchEmail_givenResponseWithSinglePrimary_expectPrimary() {
        var emailResponse = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("""
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
                        ]""")
                .build();

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(emailResponse));

        assertEquals("user@example.com", sut.fetchEmail("some-token"));
    }

    @Test
    void fetchEmail_givenEmptyResponse_expectNull() {
        var emailResponse = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("[]")
                .build();

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(emailResponse));

        assertNull(sut.fetchEmail("some-token"));
    }
}