package no.vicx.backend.calculator.vm


data class PaginatedCalculations(
    val calculations: List<CalcVm>,
    val page: Int,
    val totalPages: Int
)