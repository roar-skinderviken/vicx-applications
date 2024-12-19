package no.vicx.backend.esport;

import no.vicx.backend.esport.vm.EsportVm;
import no.vicx.backend.esport.vm.MatchType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EsportService {

    private final EsportClient esportClient;

    public EsportService(EsportClient esportClient) {
        this.esportClient = esportClient;
    }

    @Cacheable(value = "ESPORT")
    public EsportVm getMatches() {
        return new EsportVm(
                esportClient.getMatches(MatchType.running),
                esportClient.getMatches(MatchType.upcoming));
    }
}
