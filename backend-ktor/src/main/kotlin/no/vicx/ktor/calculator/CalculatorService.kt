package no.vicx.ktor.calculator

import no.vicx.ktor.calculator.vm.CalcVm
import no.vicx.ktor.calculator.vm.PaginatedCalculations
import no.vicx.ktor.db.model.CalcEntry
import no.vicx.ktor.db.model.CalculatorOperation
import no.vicx.ktor.db.repository.CalculatorRepository

class CalculatorService(
    private val calculatorRepository: CalculatorRepository,
) {
    suspend fun getPagedCalculations(page: Int): PaginatedCalculations {
        val (calculationsInPage, totalCount) =
            calculatorRepository.findAllOrderDesc(page, DEFAULT_PAGE_SIZE)

        return PaginatedCalculations(
            calculations = calculationsInPage.map { it.toGraphQLModel() },
            page = page,
            totalPages = totalPages(totalCount),
        )
    }

    suspend fun calculate(
        firstValue: Long,
        secondValue: Long,
        operation: CalculatorOperation,
        username: String?,
    ): CalcVm {
        val result =
            when (operation) {
                CalculatorOperation.PLUS -> (firstValue + secondValue)
                CalculatorOperation.MINUS -> (firstValue - secondValue)
            }

        return calculatorRepository
            .save(
                CalcEntry(
                    firstValue = firstValue,
                    secondValue = secondValue,
                    operation = operation,
                    result = result,
                    username = username,
                ),
            ).toGraphQLModel()
    }

    suspend fun isAllowedToDelete(
        idsToDelete: List<Int>,
        username: String,
    ): Boolean {
        if (idsToDelete.isEmpty()) return false

        val idsFromDatabase: Set<Int> = calculatorRepository.findAllIdsByUsername(username)
        return idsFromDatabase.containsAll(idsToDelete)
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 10

        fun totalPages(totalItems: Int): Int =
            if (totalItems % DEFAULT_PAGE_SIZE == 0) {
                totalItems / DEFAULT_PAGE_SIZE
            } else {
                totalItems / DEFAULT_PAGE_SIZE + 1
            }
    }
}
