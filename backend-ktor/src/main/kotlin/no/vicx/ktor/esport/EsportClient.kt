package no.vicx.ktor.esport

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import no.vicx.ktor.esport.vm.EsportMatchVm
import no.vicx.ktor.esport.vm.MatchType

class EsportClient(
    private val httpClient: HttpClient,
    private val token: String,
) {
    suspend fun getMatches(matchType: MatchType): List<EsportMatchVm> =
        httpClient
            .get(buildUrl(matchType)) { bearerAuth(token) }
            .body<List<EsportMatchVm>>()

    companion object {
        const val PANDASCORE_BASE_URL: String = "https://api.pandascore.co/csgo/matches"

        fun buildUrl(matchType: MatchType) =
            URLBuilder(PANDASCORE_BASE_URL)
                .apply {
                    appendPathSegments(matchType.name.lowercase())
                }.build()
    }
}
