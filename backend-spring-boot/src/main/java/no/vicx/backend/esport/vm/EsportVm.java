package no.vicx.backend.esport.vm;

import java.util.List;

public record EsportVm(
        List<EsportMatchVm> runningMatches,
        List<EsportMatchVm> upcomingMatches
) {
}
