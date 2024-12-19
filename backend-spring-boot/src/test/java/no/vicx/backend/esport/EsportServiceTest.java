package no.vicx.backend.esport;

import no.vicx.backend.esport.vm.MatchType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class EsportServiceTest {

    @Mock
    EsportClient esportClient;

    @InjectMocks
    EsportService sut;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

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