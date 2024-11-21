package no.vicx.backend.user.service;

import no.vicx.backend.user.vm.RecaptchaResponseVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class RecaptchaServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private RecaptchaService sut;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void verifyToken_givenValidToken_expectTrue() {
        when(responseSpec.bodyToMono(RecaptchaResponseVm.class)).thenReturn(Mono.just(
                new RecaptchaResponseVm(
                        true,
                        "~challenge_ts~",
                        "~hostname~",
                        new String[0]
                )
        ));

        var result = sut.verifyToken("some-token");

        assertTrue(result);
    }

    @Test
    void verifyToken_givenValidTokenButEmptyResponse_expectFalse() {
        when(responseSpec.bodyToMono(RecaptchaResponseVm.class)).thenReturn(Mono.empty());

        var result = sut.verifyToken("some-token");

        assertFalse(result);
    }

    @Test
    void verifyToken_givenValidTokenButNonSuccessResponse_expectFalse() {
        when(responseSpec.bodyToMono(RecaptchaResponseVm.class)).thenReturn(Mono.just(
                new RecaptchaResponseVm(
                        false,
                        "~challenge_ts~",
                        "~hostname~",
                        new String[] {"Some error"}
                )
        ));

        var result = sut.verifyToken("some-token");

        assertFalse(result);
    }
}
