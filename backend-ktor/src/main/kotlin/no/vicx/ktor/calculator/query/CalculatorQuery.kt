package no.vicx.ktor.calculator.query

import com.expediagroup.graphql.server.operations.Query
import no.vicx.ktor.calculator.CalculatorService
import no.vicx.ktor.calculator.vm.PaginatedCalculations

@Suppress("unused")
class CalculatorQuery(
    private val calculatorService: CalculatorService,
) : Query {
    suspend fun getAllCalculations(page: Int): PaginatedCalculations = calculatorService.getPagedCalculations(page)
}
