package no.vicx.backend.esport;

import no.vicx.backend.esport.vm.EsportMatchVm;
import no.vicx.backend.esport.vm.MatchType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public record EsportClient(
        WebClient webClient,
        @Value("${esport.token}") String token) {

    static final String PANDASCORE_BASE_URL = "https://api.pandascore.co/csgo/matches/";

    public Flux<EsportMatchVm> getMatches(final MatchType matchType) {
        var url = PANDASCORE_BASE_URL +
                matchType.name() +
                "?token=" + token;

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(EsportMatchVm.class)
                .filter(match -> match.opponents().size() == 2);
    }
}
