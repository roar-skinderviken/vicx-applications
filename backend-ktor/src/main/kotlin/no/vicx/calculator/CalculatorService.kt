package no.vicx.calculator

import no.vicx.calculator.vm.CalcVm
import no.vicx.calculator.vm.PaginatedCalculations
import no.vicx.db.model.CalcEntry
import no.vicx.db.model.CalculatorOperation
import no.vicx.db.repository.CalculatorRepository
import java.time.Duration

class CalculatorService(
    private val calculatorRepository: CalculatorRepository,
    private val maxAge: Duration
) {
    suspend fun getPagedCalculations(page: Int): PaginatedCalculations {
        val (calculationsInPage, totalCount) =
            calculatorRepository.findAllOrderDesc(page, DEFAULT_PAGE_SIZE)

        return PaginatedCalculations(
            calculations = calculationsInPage.map { it.toGraphQLModel() },
            page = page,
            totalPages = totalPages(totalCount)
        )
    }

    suspend fun calculate(
        firstValue: Long,
        secondValue: Long,
        operation: CalculatorOperation,
        username: String
    ): CalcVm {
        val result = when (operation) {
            CalculatorOperation.PLUS -> (firstValue + secondValue)
            CalculatorOperation.MINUS -> (firstValue - secondValue)
        }

        return calculatorRepository.save(
            CalcEntry(
                firstValue = firstValue,
                secondValue = secondValue,
                operation = operation,
                result = result,
                username = username,
            )
        ).toGraphQLModel()
    }

    suspend fun isAllowedToDelete(
        idsToDelete: List<Int>,
        username: String
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