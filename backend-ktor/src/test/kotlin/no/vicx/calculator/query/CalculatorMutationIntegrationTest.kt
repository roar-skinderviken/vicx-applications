package no.vicx.calculator.query

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import no.vicx.calculator.query.CalculatorMutation.Companion.ANONYMOUS_USERNAME
import no.vicx.calculator.query.wrapper.CreateCalculation
import no.vicx.calculator.query.wrapper.DeleteCalculations
import no.vicx.calculator.toGraphQLModel
import no.vicx.db.model.CalculatorOperation
import no.vicx.util.CalculatorTestUtils.calcEntryInTest
import no.vicx.util.SecurityTestUtils.USERNAME_IN_TEST
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CalculatorMutationIntegrationTest : CalculatorGraphQLTestBase() {

    @ParameterizedTest
    @ValueSource(strings = [ANONYMOUS_USERNAME, USERNAME_IN_TEST])
    fun `given valid createCalculation request then expect result`(username: String) {
        val mutation = """
            mutation {
                createCalculation(firstValue: 2, secondValue: 1, operation: PLUS) {
                    id
                    firstValue
                    secondValue
                    operation
                    result
                    username
                    createdAt
                }
            }""".trimIndent()

        val mockResult = calcEntryInTest(1).toGraphQLModel()

        coEvery {
            calculatorService.calculate(
                2, 1, CalculatorOperation.PLUS, username
            )
        } returns mockResult

        val addAuthHeader = username == USERNAME_IN_TEST
        val responseBody = postGraphQLRequestAndReturnResult<CreateCalculation>(mutation, addAuthHeader)

        assertEquals(mockResult, responseBody.data?.createCalculation)
    }

    @Nested
    inner class DeleteCalculationsTests {

        @Test
        fun `given deleteCalculations request without auth then expect error`() {
            coEvery { calculatorRepository.deleteByIdIn(listOf(1, 2)) } returns 2

            val responseBody =
                postGraphQLRequestAndReturnResult<DeleteCalculations>(deleteCalculationsMutation, false)

            assertNotNull(responseBody.errors)
            assertEquals(1, responseBody.errors.size)

            val firstError = responseBody.errors.first()
            assertEquals("Exception while fetching data (/deleteCalculations) : Unauthorized", firstError.message)

            coVerify { calculatorRepository wasNot Called }
        }

        @Test
        fun `given authenticated deleteCalculations request then expect result`() {
            coEvery { calculatorService.isAllowedToDelete(listOf(1, 2), USERNAME_IN_TEST) } returns true
            coEvery { calculatorRepository.deleteByIdIn(listOf(1, 2)) } returns 2

            val responseBody =
                postGraphQLRequestAndReturnResult<DeleteCalculations>(deleteCalculationsMutation, true)

            assertEquals(true, responseBody.data?.deleteCalculations)
        }

        @Test
        fun `given authenticated deleteCalculations request then expect error`() {
            coEvery { calculatorService.isAllowedToDelete(listOf(1, 2), USERNAME_IN_TEST) } returns false
            coEvery { calculatorRepository.deleteByIdIn(listOf(1, 2)) } returns 2

            val responseBody =
                postGraphQLRequestAndReturnResult<DeleteCalculations>(deleteCalculationsMutation, true)

            assertNotNull(responseBody.errors)
            val firstError = responseBody.errors.first()
            assertEquals("Exception while fetching data (/deleteCalculations) : Forbidden", firstError.message)

            coVerify(exactly = 0) { calculatorRepository.deleteByIdIn(any()) }
        }
    }

    companion object {
        val deleteCalculationsMutation = """
            mutation {
                deleteCalculations(ids: [1,2])
            }""".trimIndent()
    }
}