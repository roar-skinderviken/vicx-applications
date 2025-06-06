package no.vicx.esport

import com.github.benmanes.caffeine.cache.AsyncCache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import no.vicx.esport.vm.EsportMatchVm
import no.vicx.esport.vm.EsportVm
import no.vicx.esport.vm.MatchType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class EsportService(
    private val esportClient: EsportClient
) {
    private val cache: AsyncCache<MatchType, List<EsportMatchVm>> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .buildAsync()

    suspend fun getMatches(): EsportVm = coroutineScope {
        val runningMatchesDeferred = async { getCachedMatches(MatchType.running).await() }
        val upcomingMatchesDeferred = async { getCachedMatches(MatchType.upcoming).await() }

        EsportVm(
            runningMatches = runningMatchesDeferred.await(),
            upcomingMatches = upcomingMatchesDeferred.await()
        )
    }

    private suspend fun getCachedMatches(matchType: MatchType): CompletableFuture<List<EsportMatchVm>> =
        coroutineScope {
            cache.get(matchType) { _, _ ->
                future {
                    log.info("Cache miss for {}", matchType)
                    esportClient.getMatches(matchType)
                }
            }
        }

    companion object {
        val log: Logger = LoggerFactory.getLogger(EsportService::class.java)
    }
}