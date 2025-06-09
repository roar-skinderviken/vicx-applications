package no.vicx.calculator.query.wrapper

import kotlinx.serialization.Serializable
import no.vicx.calculator.vm.PaginatedCalculations

@Serializable
data class GetAllCalculations(
    val getAllCalculations: PaginatedCalculations
)