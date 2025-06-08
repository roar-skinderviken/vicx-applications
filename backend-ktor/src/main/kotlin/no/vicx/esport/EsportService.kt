package no.vicx.esport

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.vicx.cache.AsyncCacheWrapper
import no.vicx.esport.vm.EsportMatchVm
import no.vicx.esport.vm.EsportVm
import no.vicx.esport.vm.MatchType
import java.util.concurrent.TimeUnit

class EsportService(
    private val esportClient: EsportClient
) {
    private val asyncCache = AsyncCacheWrapper<MatchType, List<EsportMatchVm>>(
        1, TimeUnit.MINUTES,
    ) { matchType -> esportClient.getMatches(matchType) }

    suspend fun getMatches(): EsportVm = coroutineScope {
        val runningMatchesDeferred = async { asyncCache.getOrCompute(MatchType.RUNNING) }
        val upcomingMatchesDeferred = async { asyncCache.getOrCompute(MatchType.UPCOMING) }

        EsportVm(
            runningMatches = runningMatchesDeferred.await(),
            upcomingMatches = upcomingMatchesDeferred.await()
        )
    }
}