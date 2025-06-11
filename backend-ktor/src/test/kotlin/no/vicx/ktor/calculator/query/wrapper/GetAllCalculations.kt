package no.vicx.ktor.calculator.query.wrapper

import kotlinx.serialization.Serializable
import no.vicx.ktor.calculator.vm.PaginatedCalculations

@Serializable
data class GetAllCalculations(
    val getAllCalculations: PaginatedCalculations
)