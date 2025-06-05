package no.vicx.calculator.query

import io.ktor.server.testing.*
import io.mockk.coEvery
import no.vicx.calculator.query.wrapper.GetAllCalculations
import no.vicx.calculator.toGraphQLModel
import no.vicx.calculator.vm.PaginatedCalculations
import no.vicx.util.CalculatorTestUtils.generateTestCalcEntries
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CalculatorQueryIntegrationTest : CalculatorGraphQLTestBase() {

    @Test
    fun `given valid request to getAllCalculations then expect result`() = testApplication {
        val mockResult = PaginatedCalculations(
            calculations = generateTestCalcEntries(1).map { it.toGraphQLModel() },
            page = 1,
            totalPages = 1,
        )

        coEvery { calculatorService.getPagedCalculations(1) } returns mockResult

        val query = """
            query {
                getAllCalculations(page: 1) {
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
            }""".trimIndent()

        val responseBody = postGraphQLRequestAndReturnResult<GetAllCalculations>(query)

        assertEquals(mockResult, responseBody.data?.getAllCalculations)
    }
}