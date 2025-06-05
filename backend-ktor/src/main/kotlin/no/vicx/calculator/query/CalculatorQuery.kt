package no.vicx.calculator.query

import com.expediagroup.graphql.server.operations.Query
import no.vicx.calculator.CalculatorService
import no.vicx.calculator.vm.PaginatedCalculations

@Suppress("unused")
class CalculatorQuery(
    private val calculatorService: CalculatorService
) : Query {

    suspend fun getAllCalculations(page: Int): PaginatedCalculations =
        calculatorService.getPagedCalculations(page)
}