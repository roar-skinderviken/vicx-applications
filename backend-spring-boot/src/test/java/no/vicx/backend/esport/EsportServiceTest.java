package no.vicx.backend.esport;

import no.vicx.backend.esport.vm.MatchType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
        when(esportClient.getMatches(any())).thenReturn(new ArrayList<>());

        sut.getMatches();

        verify(esportClient).getMatches(MatchType.running);
        verify(esportClient).getMatches(MatchType.upcoming);
    }
}