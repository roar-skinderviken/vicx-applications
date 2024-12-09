package no.vicx.backend.user.service;

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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class RecaptchaServiceTest {

    @Mock
    ExchangeFunction exchangeFunction;

    RecaptchaService sut;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);

        var webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        sut = new RecaptchaService(webClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void verifyToken_givenValidToken_expectTrue() {
        var tokenResponse = buildClientResponse("""
                        {
                            "success": true,
                            "challenge_ts": "~challenge_ts~",
                            "hostname": "~hostname~",
                            "errorCodes": []
                        }""");

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(tokenResponse));

        var result = sut.verifyToken("some-token");

        assertTrue(result);
    }

    @Test
    void verifyToken_givenErrorResponse_expectFalse() {
        var tokenResponse = buildClientResponse("""
                        {
                            "success": false,
                            "challenge_ts": "~challenge_ts~",
                            "hostname": "~hostname~",
                            "errorCodes": ["Some error"]
                        }""");

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(tokenResponse));

        var result = sut.verifyToken("some-token");

        assertFalse(result);
    }

    @Test
    void verifyToken_givenNullResponseBody_expectFalse() {
        var tokenResponse = buildClientResponse(null);

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(tokenResponse));

        var result = sut.verifyToken("some-token");

        assertFalse(result);
    }

    @Test
    void verifyToken_givenEmptyResponseBody_expectFalse() {
        var tokenResponse = buildClientResponse("{}");

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(tokenResponse));

        var result = sut.verifyToken("some-token");

        assertFalse(result);
    }

    private static ClientResponse buildClientResponse(String body) {
        var builder = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        if (body != null){
            builder.body(body);
        }

        return builder.build();
    }
}
