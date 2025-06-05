package no.vicx.calculator.vm

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedCalculations(
    val calculations: List<CalcVm>,
    val page: Int,
    val totalPages: Int
)
