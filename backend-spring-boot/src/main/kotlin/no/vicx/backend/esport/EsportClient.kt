package no.vicx.backend.esport

import no.vicx.backend.esport.vm.EsportMatchVm
import no.vicx.backend.esport.vm.MatchType
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.Flux


@Service
class EsportClient(
    private val webClient: WebClient,
    @Value("\${esport.token}") private val token: String
) {
    fun getMatches(matchType: MatchType): Flux<EsportMatchVm> = webClient
        .get()
        .uri("$PANDASCORE_BASE_URL${matchType.name.lowercase()}")
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.AUTHORIZATION, "$BEARER_PREFIX$token")
        .retrieve()
        .bodyToFlux<EsportMatchVm>()

    companion object {
        private const val PANDASCORE_BASE_URL = "https://api.pandascore.co/csgo/matches/"
        private const val BEARER_PREFIX = "Bearer "
    }
}
