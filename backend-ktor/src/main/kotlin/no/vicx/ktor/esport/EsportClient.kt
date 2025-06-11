package no.vicx.ktor.esport

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import no.vicx.ktor.esport.vm.EsportMatchVm
import no.vicx.ktor.esport.vm.MatchType

class EsportClient(
    private val httpClient: HttpClient,
    private val token: String
) {
    suspend fun getMatches(matchType: MatchType): List<EsportMatchVm> {
        val url = "$PANDASCORE_BASE_URL${matchType.name.lowercase()}"

        return httpClient
            .get(url) { bearerAuth(token) }
            .body<List<EsportMatchVm>>()
    }

    companion object {
        const val PANDASCORE_BASE_URL: String = "https://api.pandascore.co/csgo/matches/"
    }
}