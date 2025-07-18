package no.vicx.ktor.esport.vm

import kotlinx.serialization.Serializable

@Serializable
data class EsportVm(
    val runningMatches: List<EsportMatchVm>,
    val upcomingMatches: List<EsportMatchVm>,
)
