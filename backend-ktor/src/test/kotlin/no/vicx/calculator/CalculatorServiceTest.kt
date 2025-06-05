package no.vicx.calculator

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.toKotlinLocalDateTime
import no.vicx.calculator.CalculatorService.Companion.DEFAULT_PAGE_SIZE
import no.vicx.db.model.CalcEntry
import no.vicx.db.model.CalculatorOperation
import no.vicx.db.repository.CalculatorRepository
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class CalculatorServiceTest {

    @Mock
    private lateinit var calculatorRepository: CalculatorRepository

    @Mock
    private lateinit var maxAge: Duration

    @InjectMocks
    lateinit var sut: CalculatorService

    @Nested
    inner class CreateCalculationTests {

        @ParameterizedTest
        @EnumSource(CalculatorOperation::class)
        fun `given valid params when calling calculate then expect call to CalculatorRepository#save`(
            expectedOperation: CalculatorOperation
        ) =
            runTest {
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

                whenever(calculatorRepository.save(any<CalcEntry>())).thenReturn(expected)

                sut.calculate(
                    firstValue = expected.firstValue,
                    secondValue = expected.secondValue,
                    operation = expected.operation,
                    username = expected.username
                )

                verify(calculatorRepository).save(argThat {
                    this.firstValue == expected.firstValue &&
                            this.secondValue == expected.secondValue &&
                            this.operation == expected.operation &&
                            this.result == expected.result &&
                            this.username == expected.username
                })
            }
    }

    @Nested
    inner class GetPagedCalculationsTests {

        @Test
        fun `given result from repo when calling getPagedCalculations then expect result`() = runTest {
            val expectedPageNumber = 2
            val expectedTotalCount = 101
            val expectedTotalPages = 11

            whenever(calculatorRepository.findAllOrderDesc(expectedPageNumber, DEFAULT_PAGE_SIZE))
                .thenReturn(Pair(createCalcEntriesInTest(DEFAULT_PAGE_SIZE), expectedTotalCount))

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

            whenever(calculatorRepository.findAllOrderDesc(expectedPageNumber, DEFAULT_PAGE_SIZE))
                .thenReturn(Pair(createCalcEntriesInTest(DEFAULT_PAGE_SIZE), expectedTotalCount))

            val paginatedCalculations = sut.getPagedCalculations(expectedPageNumber)

            assertEquals(expectedTotalPages, paginatedCalculations.totalPages)
        }
    }

    companion object {
        fun createCalcEntriesInTest(size: Int) = List<CalcEntry>(size) { index ->
            CalcEntry(
                index.toLong(), 42, 43, CalculatorOperation.PLUS,
                85,
                "~username~",
                LocalDateTime.now().plusSeconds(index.toLong()).toKotlinLocalDateTime()
            )
        }.sortedByDescending(CalcEntry::id)
    }
}