package no.vicx.backend.esport.vm

data class EsportVm(
    val runningMatches: List<EsportMatchVm>,
    val upcomingMatches: List<EsportMatchVm>
)
