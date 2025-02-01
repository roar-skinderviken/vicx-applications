package no.vicx.backend.esport;

import no.vicx.backend.esport.vm.MatchType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EsportServiceTest {

    @Mock
    EsportClient esportClient;

    @InjectMocks
    EsportService sut;

    @Test
    void getMatches_expectResult() {
        when(esportClient.getMatches(any())).thenReturn(Flux.empty());

        var matches = sut.getMatches();

        StepVerifier.create(matches)
                .expectNextCount(1)
                .verifyComplete();

        verify(esportClient).getMatches(MatchType.running);
        verify(esportClient).getMatches(MatchType.upcoming);
    }
}