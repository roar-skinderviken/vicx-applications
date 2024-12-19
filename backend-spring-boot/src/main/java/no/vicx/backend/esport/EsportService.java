package no.vicx.backend.esport;

import no.vicx.backend.esport.vm.EsportVm;
import no.vicx.backend.esport.vm.MatchType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EsportService {

    private final EsportClient esportClient;

    public EsportService(EsportClient esportClient) {
        this.esportClient = esportClient;
    }

    @Cacheable(value = "ESPORT")
    public Mono<EsportVm> getMatches() {
        return Mono.zip(
                esportClient.getMatches(MatchType.running).collectList(),
                esportClient.getMatches(MatchType.upcoming).collectList()
        ).map(tuple -> new EsportVm(
                tuple.getT1(),
                tuple.getT2()));
    }
}
