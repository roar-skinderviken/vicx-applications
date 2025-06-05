package no.vicx.calculator

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.toKotlinLocalDateTime
import no.vicx.calculator.CalculatorService.Companion.DEFAULT_PAGE_SIZE
import no.vicx.db.model.CalcEntry
import no.vicx.db.model.CalculatorOperation
import no.vicx.db.repository.CalculatorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.Duration
import java.time.LocalDateTime

class CalculatorServiceTest {

    private val calculatorRepository = mockk<CalculatorRepository>(relaxed = true)
    private val maxAge = mockk<Duration>(relaxed = true)
    private val sut = CalculatorService(calculatorRepository, maxAge)

    @Nested
    inner class CreateCalculationTests {

        @ParameterizedTest
        @EnumSource(CalculatorOperation::class)
        fun `given valid params when calling calculate then expect call to CalculatorRepository#save`(
            expectedOperation: CalculatorOperation
        ) = runTest {
            val expectedResult = when (expectedOperation) {
                CalculatorOperation.PLUS -> 3L
                CalculatorOperation.MINUS -> 1L
            }

            val expected = CalcEntry(
                firstValue = 2L,
                secondValue = 1L,
                operation = expectedOperation,
                result = expectedResult,
                username = "~username~"
            )

            coEvery { calculatorRepository.save(any<CalcEntry>()) } returns expected

            sut.calculate(
                firstValue = expected.firstValue,
                secondValue = expected.secondValue,
                operation = expected.operation,
                username = expected.username
            )

            coVerify(exactly = 1) {
                calculatorRepository.save(match {
                    it.firstValue == expected.firstValue &&
                            it.secondValue == expected.secondValue &&
                            it.operation == expected.operation &&
                            it.result == expected.result &&
                            it.username == expected.username
                })
            }
        }
    }

    @Nested
    inner class GetPagedCalculationsTests {

        @Test
        fun `given result from repo when calling getPagedCalculations then expect result`() = runTest {
            val expectedPageNumber = 2
            val expectedTotalCount = 101
            val expectedTotalPages = 11

            coEvery {
                calculatorRepository.findAllOrderDesc(expectedPageNumber, DEFAULT_PAGE_SIZE)
            } returns Pair(createCalcEntriesInTest(DEFAULT_PAGE_SIZE), expectedTotalCount)

            val paginatedCalculations = sut.getPagedCalculations(expectedPageNumber)

            assertEquals(DEFAULT_PAGE_SIZE, paginatedCalculations.calculations.size)
            assertEquals(expectedPageNumber, paginatedCalculations.page)
            assertEquals(expectedTotalPages, paginatedCalculations.totalPages)
        }

        @ParameterizedTest
        @ValueSource(ints = [100, 101])
        fun `given result from repo when calling getPagedCalculations then expect total pages`(
            expectedTotalCount: Int
        ) = runTest {
            val expectedPageNumber = 1
            val expectedTotalPages = if (expectedTotalCount % DEFAULT_PAGE_SIZE == 0)
                expectedTotalCount / DEFAULT_PAGE_SIZE
            else
                expectedTotalCount / DEFAULT_PAGE_SIZE + 1

            coEvery {
                calculatorRepository.findAllOrderDesc(expectedPageNumber, DEFAULT_PAGE_SIZE)
            } returns Pair(createCalcEntriesInTest(DEFAULT_PAGE_SIZE), expectedTotalCount)

            val paginatedCalculations = sut.getPagedCalculations(expectedPageNumber)

            assertEquals(expectedTotalPages, paginatedCalculations.totalPages)
        }
    }

    companion object {
        fun createCalcEntriesInTest(size: Int) = List(size) { index ->
            CalcEntry(
                index.toLong(), 42, 43, CalculatorOperation.PLUS,
                85,
                "~username~",
                LocalDateTime.now().plusSeconds(index.toLong()).toKotlinLocalDateTime()
            )
        }.sortedByDescending(CalcEntry::id)
    }
}
