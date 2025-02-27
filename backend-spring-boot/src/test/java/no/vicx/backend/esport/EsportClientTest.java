package no.vicx.backend.esport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.vicx.backend.esport.vm.EsportMatchVm;
import no.vicx.backend.esport.vm.MatchType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EsportClientTest {

    @Mock
    ExchangeFunction exchangeFunction;

    EsportClient sut;

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        sut = new EsportClient(webClient, "~token~");
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void getMatches_givenMatches_expectResult() throws JsonProcessingException {
        var expectedMatch = new EsportMatchVm(
                42L,
                "Team 1 vs Team 2",
                "01/01/2024",
                "running");

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(MAPPER.writeValueAsString(Collections.singletonList(expectedMatch)))
                        .build()));

        var runningMatches = sut.getMatches(MatchType.running);

        StepVerifier.create(runningMatches)
                .expectNext(expectedMatch)
                .verifyComplete();
    }

    @Test
    void getMatches_givenNoMatches_expectEmptyList() {
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header(HttpHeaders.ACCEPT_ENCODING)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("[]")
                        .build()));


        var runningMatches = sut.getMatches(MatchType.running);

        StepVerifier.create(runningMatches)
                .verifyComplete();
    }
}