package no.vicx.calculator

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.toKotlinLocalDateTime
import no.vicx.calculator.CalculatorService.Companion.DEFAULT_PAGE_SIZE
import no.vicx.db.model.CalcEntry
import no.vicx.db.model.CalculatorOperation
import no.vicx.db.repository.CalculatorRepository
import java.time.Duration
import java.time.LocalDateTime

class CalculatorServiceTest : BehaviorSpec() {
    init {
        coroutineTestScope = true

        lateinit var calculatorRepository: CalculatorRepository
        lateinit var sut: CalculatorService
        val maxAge = mockk<Duration>(relaxed = true)

        Given("a CalculatorService with mocked CalculatorRepository") {
            beforeContainer {
                calculatorRepository = mockk()
                sut = CalculatorService(calculatorRepository, maxAge)
            }

            withData(
                CalculatorOperation.PLUS to 3L,
                CalculatorOperation.MINUS to 1L,
            ) { (operation, expectedResult) ->

                When("calculate is called with valid parameters, operator: $operation") {
                    val expected = expectedCalcEntry(operation, expectedResult)
                    coEvery { calculatorRepository.save(any<CalcEntry>()) } returns expected

                    sut.calculate(
                        firstValue = expected.firstValue,
                        secondValue = expected.secondValue,
                        operation = expected.operation,
                        username = expected.username
                    )

                    Then("it should save the correct calculation entry in the repository") {
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
            }

            forAll(
                row(100, 10),
                row(101, 11),
            ) { expectedTotalCount, expectedTotalPages ->

                When("getPagedCalculations is called and totalCount: $expectedTotalCount and totalPages: $expectedTotalPages") {
                    val expectedPageNumber = 2

                    coEvery {
                        calculatorRepository.findAllOrderDesc(expectedPageNumber, DEFAULT_PAGE_SIZE)
                    } returns Pair(createCalcEntriesInTest(DEFAULT_PAGE_SIZE), expectedTotalCount)

                    val paginatedCalculations = sut.getPagedCalculations(expectedPageNumber)

                    Then("it should return the expected number of calculations and pages") {
                        paginatedCalculations.calculations shouldHaveSize DEFAULT_PAGE_SIZE
                        paginatedCalculations.page shouldBe expectedPageNumber
                        paginatedCalculations.totalPages shouldBe expectedTotalPages
                    }
                }
            }
        }
    }

    companion object {
        fun expectedCalcEntry(
            operation: CalculatorOperation,
            result: Long
        ) = CalcEntry(
            firstValue = 2L,
            secondValue = 1L,
            operation = operation,
            result = result,
            username = "~username~"
        )

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
