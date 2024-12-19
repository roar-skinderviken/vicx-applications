package no.vicx.backend.esport;

import no.vicx.backend.esport.vm.EsportMatchVm;
import no.vicx.backend.esport.vm.MatchType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
public record EsportClient(
        RestClient restClient,
        @Value("${esport.token}") String token) {

    static final String PANDASCORE_BASE_URL = "https://api.pandascore.co/csgo/matches/";

    public List<EsportMatchVm> getMatches(final MatchType matchType) {
        var url = PANDASCORE_BASE_URL +
                matchType.name() +
                "?token=" + token;

        var matches = restClient
                .get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<EsportMatchVm>>() {
                });

        if (matches == null) {
            return Collections.emptyList();
        }

        return matches.stream()
                .filter(match -> match.opponents().size() == 2)
                .toList();
    }
}
