package no.vicx.backend.esport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.vicx.backend.config.RestClientConfig;
import no.vicx.backend.esport.vm.EsportMatchVm;
import no.vicx.backend.esport.vm.EsportTeamVm;
import no.vicx.backend.esport.vm.MatchType;
import no.vicx.backend.esport.vm.OpponentVm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Collections;
import java.util.List;

import static no.vicx.backend.esport.EsportClient.PANDASCORE_BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(EsportClient.class)
@Import(RestClientConfig.class)
class EsportClientTest {

    @Value("${esport.token}")
    String token;

    @Autowired
    MockRestServiceServer mockServer;

    @Autowired
    EsportClient sut;

    @Autowired
    ObjectMapper objectMapper;

    public static final String URL_IN_TEST = PANDASCORE_BASE_URL + "running?token=";

    @Test
    void getMatches_givenMatches_expectResult() throws JsonProcessingException {
        var expectedMatches = Collections.singletonList(
                new EsportMatchVm(
                        "01/01/2024", "running",
                        List.of(
                                new OpponentVm(new EsportTeamVm("Team-1")),
                                new OpponentVm(new EsportTeamVm("Team-2")))));


        var body = objectMapper.writeValueAsString(expectedMatches);

        mockServer.expect(requestTo(URL_IN_TEST + token))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        var actualMatches = sut.getMatches(MatchType.running);

        assertEquals(expectedMatches, actualMatches);
    }

    @Test
    void getMatches_givenNoMatches_expectEmptyList() {
        mockServer.expect(requestTo(URL_IN_TEST + token))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        var actualMatches = sut.getMatches(MatchType.running);

        assertTrue(actualMatches.isEmpty());
    }

    @Test
    void getMatches_givenOnlySingleOpponent_expectEmptyResult() throws JsonProcessingException {
        var matches = Collections.singletonList(
                new EsportMatchVm(
                        "01/01/2024", "running",
                        List.of(new OpponentVm(new EsportTeamVm("Team-2")))));


        var body = objectMapper.writeValueAsString(matches);

        mockServer.expect(requestTo(PANDASCORE_BASE_URL + "upcoming?token=" + token))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        var actualMatches = sut.getMatches(MatchType.upcoming);

        assertTrue(actualMatches.isEmpty());
    }
}