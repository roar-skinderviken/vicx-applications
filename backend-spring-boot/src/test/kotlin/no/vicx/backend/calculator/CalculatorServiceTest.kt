package no.vicx.backend.calculator

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row6
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.vicx.backend.calculator.CalculatorTestUtils.createCalcEntryInTest
import no.vicx.database.calculator.CalcEntry
import no.vicx.database.calculator.CalculatorOperation
import no.vicx.database.calculator.CalculatorRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class CalculatorServiceTest : BehaviorSpec({
    Context("CalculatorService with mocked CalculatorRepository") {

        val calculatorRepository: CalculatorRepository = mockk(relaxed = true)
        val sut = CalculatorService(calculatorRepository, 1.minutes.toJavaDuration())

        Given("a calculatorRepository with some calc entries") {
            val calcEntry = createCalcEntryInTest()
            val page = PageImpl(
                listOf(calcEntry),
                PageRequest.of(0, 10, Sort.by("id").descending()),
                1
            )

            every { calculatorRepository.findAllBy(any()) } returns page

            When("calling getAllCalculations") {
                val result = sut.getAllCalculations(0)

                Then("result should not be null") {
                    result shouldNotBe null
                }

                Then("repository findAllBy should be called") {
                    verify { calculatorRepository.findAllBy(any()) }
                }
            }
        }

        Given("a calculatorRepository for delete operations") {
            When("calling deleteByIds with a list of IDs") {
                val idsToDelete = listOf(1L)
                sut.deleteByIds(idsToDelete)

                Then("repository deleteByIdIn should be called") {
                    verify { calculatorRepository.deleteByIdIn(idsToDelete) }
                }
            }

            When("calling deleteOldAnonymousCalculations") {
                sut.deleteOldAnonymousCalculations()

                Then("repository deleteAllByCreatedAtBeforeAndUsernameNull should be called") {
                    verify { calculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(any()) }
                }
            }
        }

        Given("a calculatorRepository with a mocked save method") {
            val slot = slot<CalcEntry>()
            every { calculatorRepository.save(capture(slot)) } answers {
                slot.captured.apply {
                    id = 1L
                    createdAt = LocalDateTime.now()
                }
            }

            forAll(
                Row6("plus operation with username", 1L, 2L, CalculatorOperation.PLUS, "user1", 3L),
                Row6("minus operation with username", 4L, 3L, CalculatorOperation.MINUS, "user1", 1L),
                Row6("plus operation without username", 5L, 6L, CalculatorOperation.PLUS, null, 11L),
                Row6("minus operation without username", 8L, 6L, CalculatorOperation.MINUS, null, 2L),
            ) { description, first, second, operation, expectedUsername, expectedResult ->
                When("calling calculate: $description") {
                    val calcVm = sut.calculate(first, second, operation, expectedUsername)

                    Then("result should be as expected") {
                        assertSoftly(calcVm) {
                            result shouldBe expectedResult
                            username shouldBe expectedUsername
                        }
                    }
                }
            }
        }
    }
})
