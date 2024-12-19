package no.vicx.backend.esport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.vicx.backend.esport.vm.EsportMatchVm;
import no.vicx.backend.esport.vm.EsportTeamVm;
import no.vicx.backend.esport.vm.MatchType;
import no.vicx.backend.esport.vm.OpponentVm;
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
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class EsportClientTest {

    @Mock
    ExchangeFunction exchangeFunction;

    EsportClient sut;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        sut = new EsportClient(webClient, "~token~");
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void getMatches_givenMatches_expectResult() throws JsonProcessingException {
        var expectedMatch = new EsportMatchVm(
                "01/01/2024", "running",
                List.of(
                        new OpponentVm(new EsportTeamVm("Team-1")),
                        new OpponentVm(new EsportTeamVm("Team-2"))));

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
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("[]")
                        .build()));


        var runningMatches = sut.getMatches(MatchType.running);

        StepVerifier.create(runningMatches)
                .verifyComplete();
    }

    @Test
    void getMatches_givenOnlySingleOpponent_expectEmptyResult() throws JsonProcessingException {
        var matchInTest = new EsportMatchVm(
                "01/01/2024", "running",
                List.of(new OpponentVm(new EsportTeamVm("Team-2"))));

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(MAPPER.writeValueAsString(Collections.singletonList(matchInTest)))
                        .build()));

        var runningMatches = sut.getMatches(MatchType.running);

        StepVerifier.create(runningMatches)
                .verifyComplete();
    }
}