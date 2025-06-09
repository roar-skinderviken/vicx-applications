package no.vicx.calculator.query

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.serialization.json.Json
import no.vicx.calculator.CalculatorService
import no.vicx.calculator.query.CalculatorMutation.Companion.ANONYMOUS_USERNAME
import no.vicx.calculator.query.wrapper.CreateCalculation
import no.vicx.calculator.query.wrapper.DeleteCalculations
import no.vicx.calculator.query.wrapper.GetAllCalculations
import no.vicx.calculator.query.wrapper.GraphQLResponseBody
import no.vicx.calculator.toGraphQLModel
import no.vicx.calculator.vm.PaginatedCalculations
import no.vicx.db.model.CalculatorOperation
import no.vicx.db.repository.CalculatorRepository
import no.vicx.plugins.configureGraphQL
import no.vicx.util.CalculatorTestUtils.calcEntryInTest
import no.vicx.util.CalculatorTestUtils.generateTestCalcEntries
import no.vicx.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.util.SecurityTestUtils.configureTestSecurity
import no.vicx.util.SecurityTestUtils.tokenStringInTest

class CalculatorQueryTest : BehaviorSpec() {
    private val calculatorService: CalculatorService = mockk()
    private val calculatorRepository: CalculatorRepository = mockk()

    init {
        coroutineTestScope = true

        Given("GraphQL environment with mocked dependencies") {
            beforeContainer {
                clearAllMocks()
            }

            When("sending valid request to getAllCalculations") {
                val expectedPaginatedCalculations = PaginatedCalculations(
                    calculations = generateTestCalcEntries(1).map { it.toGraphQLModel() },
                    page = 1,
                    totalPages = 1,
                )

                coEvery { calculatorService.getPagedCalculations(1) } returns expectedPaginatedCalculations

                val responseBody = postGraphQLRequestAndReturnResult<GetAllCalculations>(getAllCalculationsQuery)

                Then("expect result") {
                    responseBody.data?.getAllCalculations shouldBe expectedPaginatedCalculations
                }
            }

            forAll(
                row(ANONYMOUS_USERNAME, false),
                row(USERNAME_IN_TEST, true),
            ) { username, addAuthHeader ->
                When("sending valid request to createCalculation. $username, $addAuthHeader") {
                    val expectedCalcEntry = calcEntryInTest(username = username).toGraphQLModel()

                    coEvery {
                        calculatorService.calculate(2, 1, CalculatorOperation.PLUS, username)
                    } returns expectedCalcEntry

                    val responseBody = postGraphQLRequestAndReturnResult<CreateCalculation>(
                        createCalculationMutation, addAuthHeader
                    )

                    Then("expect result") {
                        responseBody.data?.createCalculation shouldBe expectedCalcEntry
                    }
                }
            }

            When("deleteCalculations request without auth") {
                val responseBody = postGraphQLRequestAndReturnResult<DeleteCalculations>(
                    deleteCalculationsMutation, false
                )

                Then("expect Unauthorized") {
                    responseBody.errors.shouldNotBeNull().apply {
                        size shouldBeGreaterThan 0
                        first().message shouldContain "Exception while fetching data (/deleteCalculations) : Unauthorized"
                    }

                    coVerify { calculatorRepository wasNot Called }
                }
            }

            When("deleteCalculations request for other user") {
                coEvery { calculatorService.isAllowedToDelete(listOf(1, 2), USERNAME_IN_TEST) } returns false

                val responseBody = postGraphQLRequestAndReturnResult<DeleteCalculations>(
                    deleteCalculationsMutation, true
                )

                Then("expect Forbidden") {
                    responseBody.errors.shouldNotBeNull().apply {
                        size shouldBeGreaterThan 0
                        first().message shouldContain "Exception while fetching data (/deleteCalculations) : Forbidden"
                    }

                    coVerify { calculatorRepository wasNot Called }
                }
            }

            When("valid deleteCalculations request") {
                coEvery { calculatorService.isAllowedToDelete(listOf(1, 2), USERNAME_IN_TEST) } returns true
                coEvery { calculatorRepository.deleteByIdIn(listOf(1, 2)) } returns 2

                val responseBody = postGraphQLRequestAndReturnResult<DeleteCalculations>(
                    deleteCalculationsMutation, true
                )

                Then("expect success") {
                    responseBody.data?.deleteCalculations shouldBe true
                }
            }
        }
    }

    private suspend inline fun <reified T : Any> postGraphQLRequestAndReturnResult(
        query: String,
        addAuthHeader: Boolean = false,
    ): GraphQLResponseBody<T> {
        val response = postGraphQLRequest(query, addAuthHeader)
        response.status shouldBe HttpStatusCode.OK
        return response.body<GraphQLResponseBody<T>>()
    }

    private fun postGraphQLRequest(
        query: String,
        addAuthHeader: Boolean = false,
    ): HttpResponse {
        lateinit var response: HttpResponse

        testApplication {
            application {
                configureTestSecurity()
                configureGraphQL(
                    calculatorService,
                    calculatorRepository
                )
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            response = client.post("/graphql") {
                if (addAuthHeader) bearerAuth(tokenStringInTest)
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(mapOf("query" to query)))
            }
        }

        return response
    }

    companion object {
        val getAllCalculationsQuery = """
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

        val createCalculationMutation = """
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

        val deleteCalculationsMutation = """
            mutation {
                deleteCalculations(ids: [1,2])
            }""".trimIndent()
    }
}