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
    webClientBuilder: WebClient.Builder,
    @Value("\${esport.token}") private val token: String
) {
    private val webClient = webClientBuilder
        .baseUrl(PANDASCORE_BASE_URL)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "$BEARER_PREFIX$token")
        .build()

    fun getMatches(matchType: MatchType): Flux<EsportMatchVm> = webClient
        .get()
        .uri("/${matchType.name.lowercase()}")
        .retrieve()
        .bodyToFlux<EsportMatchVm>()

    companion object {
        private const val PANDASCORE_BASE_URL = "https://api.pandascore.co/csgo/matches"
        private const val BEARER_PREFIX = "Bearer "
    }
}
