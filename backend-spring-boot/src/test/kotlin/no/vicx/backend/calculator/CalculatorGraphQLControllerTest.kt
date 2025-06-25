package no.vicx.backend.calculator

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import no.vicx.backend.calculator.vm.CalcVm
import no.vicx.backend.calculator.vm.PaginatedCalculations
import no.vicx.database.calculator.CalculatorOperation
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.graphql.test.tester.GraphQlTester
import java.time.LocalDateTime

@GraphQlTest(value = [CalculatorGraphQLController::class])
class CalculatorGraphQLControllerTest(
    graphQlTester: GraphQlTester,
    @MockkBean private val calculatorService: CalculatorService,
) : BehaviorSpec({
        Given("a mocked GraphQL controller") {
            every { calculatorService.getAllCalculations(0) } returns
                PageImpl(
                    calcVmList,
                    PageRequest.of(0, 10),
                    calcVmList.size.toLong(),
                )

            val expected = PaginatedCalculations(calcVmList, 0, 1)

            Then("Should return the correct result") {
                graphQlTester
                    .document(QUERY_STRING)
                    .execute()
                    .path("data.getAllCalculations")
                    .entity(PaginatedCalculations::class.java)
                    .isEqualTo(expected)
            }
        }
    }) {
    companion object {
        const val QUERY_STRING = """
            {
              getAllCalculations(page: 0) {
                calculations {
                  id
                  firstValue
                  secondValue
                  operation
                  result
                  username
                  createdAt
                }
                page
                totalPages
              }
            }
            """

        val calcVmList =
            listOf(
                CalcVm(
                    id = 1,
                    firstValue = 1,
                    secondValue = 2,
                    operation = CalculatorOperation.PLUS,
                    result = 3,
                    username = "user1",
                    createdAt = LocalDateTime.now(),
                ),
            )
    }
}
