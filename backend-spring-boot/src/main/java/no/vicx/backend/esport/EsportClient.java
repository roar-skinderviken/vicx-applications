package no.vicx.backend.esport;

import no.vicx.backend.esport.vm.EsportMatchVm;
import no.vicx.backend.esport.vm.MatchType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public record EsportClient(
        WebClient webClient,
        @Value("${esport.token}") String token) {

    private static final String PANDASCORE_BASE_URL = "https://api.pandascore.co/csgo/matches/";
    private static final String BEARER_PREFIX = "Bearer ";

    public Flux<EsportMatchVm> getMatches(final MatchType matchType) {
        var url = PANDASCORE_BASE_URL + matchType.name();

        return webClient
                .get()
                .uri(url)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                .retrieve()
                .bodyToFlux(EsportMatchVm.class);
    }
}
