package no.vicx.backend.esport

import no.vicx.backend.esport.vm.EsportVm
import no.vicx.backend.esport.vm.MatchType
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class EsportService(
    private val esportClient: EsportClient,
) {
    @Cacheable(value = ["ESPORT"])
    fun getMatches(): Mono<EsportVm> =
        Mono
            .zip(
                esportClient.getMatches(MatchType.RUNNING).collectList(),
                esportClient.getMatches(MatchType.UPCOMING).collectList(),
            ).map { tuple -> EsportVm(tuple.t1, tuple.t2) }
}
