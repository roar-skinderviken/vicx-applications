package no.vicx.ktor.calculator.query

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row2
import io.kotest.data.forAll
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.json.Json
import no.vicx.ktor.calculator.CalculatorService
import no.vicx.ktor.calculator.query.wrapper.CreateCalculation
import no.vicx.ktor.calculator.query.wrapper.DeleteCalculations
import no.vicx.ktor.calculator.query.wrapper.GetAllCalculations
import no.vicx.ktor.calculator.query.wrapper.GraphQLResponseBody
import no.vicx.ktor.calculator.toGraphQLModel
import no.vicx.ktor.calculator.vm.PaginatedCalculations
import no.vicx.ktor.db.model.CalculatorOperation
import no.vicx.ktor.db.repository.CalculatorRepository
import no.vicx.ktor.plugins.configureGraphQL
import no.vicx.ktor.util.CalculatorTestUtils.calcEntryInTest
import no.vicx.ktor.util.CalculatorTestUtils.generateTestCalcEntries
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.SecurityTestUtils.configureTestSecurity
import no.vicx.ktor.util.SecurityTestUtils.tokenStringInTest

class CalculatorQueryTest : BehaviorSpec() {
    private val mockCalculatorService: CalculatorService = mockk()
    private val mockCalculatorRepository: CalculatorRepository = mockk()

    init {
        Given("GraphQL environment with mocked dependencies") {
            beforeContainer {
                clearAllMocks()
            }

            When("sending valid request to getAllCalculations") {
                val expectedPaginatedCalculations =
                    PaginatedCalculations(
                        calculations = generateTestCalcEntries(1).map { it.toGraphQLModel() },
                        page = 1,
                        totalPages = 1,
                    )

                coEvery { mockCalculatorService.getPagedCalculations(1) } returns expectedPaginatedCalculations

                val responseBody = performGraphQLPostAndAssertStatus<GetAllCalculations>(getAllCalculationsQuery)

                Then("the response body should contain JSON for paginated calculations") {
                    responseBody.data?.getAllCalculations shouldBe expectedPaginatedCalculations
                }
            }

            forAll(
                Row2(null, false),
                Row2(USERNAME_IN_TEST, true),
            ) { username, addAuthHeader ->
                When("sending valid request to createCalculation. $username, $addAuthHeader") {
                    val expectedCalcEntry = calcEntryInTest(username = username).toGraphQLModel()

                    coEvery {
                        mockCalculatorService.calculate(2, 1, CalculatorOperation.PLUS, username)
                    } returns expectedCalcEntry

                    val responseBody =
                        performGraphQLPostAndAssertStatus<CreateCalculation>(
                            createCalculationMutation,
                            addAuthHeader,
                        )

                    Then("the response body should contain JSON for calculation result") {
                        responseBody.data?.createCalculation shouldBe expectedCalcEntry
                    }
                }
            }

            When("deleteCalculations request without auth") {
                val responseBody =
                    performGraphQLPostAndAssertStatus<DeleteCalculations>(
                        deleteCalculationsMutation,
                        false,
                    )

                Then("the response body should contain Unauthorized exception") {
                    responseBody.errors.shouldNotBeNull().apply {
                        size shouldBeGreaterThan 0
                        first().message shouldContain "Exception while fetching data (/deleteCalculations) : Unauthorized"
                    }

                    coVerify { mockCalculatorRepository wasNot Called }
                }
            }

            When("deleteCalculations request for calculations belonging to other user") {
                coEvery { mockCalculatorService.isAllowedToDelete(listOf(1, 2), USERNAME_IN_TEST) } returns false

                val responseBody =
                    performGraphQLPostAndAssertStatus<DeleteCalculations>(
                        deleteCalculationsMutation,
                        true,
                    )

                Then("the response body should contain Forbidden exception") {
                    responseBody.errors.shouldNotBeNull().apply {
                        size shouldBeGreaterThan 0
                        first().message shouldContain "Exception while fetching data (/deleteCalculations) : Forbidden"
                    }

                    coVerify { mockCalculatorRepository wasNot Called }
                }
            }

            When("valid deleteCalculations request") {
                coEvery { mockCalculatorService.isAllowedToDelete(listOf(1, 2), USERNAME_IN_TEST) } returns true
                coEvery { mockCalculatorRepository.deleteByIdIn(listOf(1, 2)) } returns 2

                val responseBody =
                    performGraphQLPostAndAssertStatus<DeleteCalculations>(
                        deleteCalculationsMutation,
                        true,
                    )

                Then("the response body should contain deleteCalculations: true") {
                    responseBody.data?.deleteCalculations shouldBe true
                }
            }
        }
    }

    private suspend inline fun <reified T : Any> performGraphQLPostAndAssertStatus(
        query: String,
        addAuthHeader: Boolean = false,
    ): GraphQLResponseBody<T> {
        val response = performGraphQLPost(query, addAuthHeader)
        response.status shouldBe HttpStatusCode.OK
        return response.body<GraphQLResponseBody<T>>()
    }

    private fun performGraphQLPost(
        query: String,
        addAuthHeader: Boolean = false,
    ): HttpResponse {
        lateinit var response: HttpResponse

        testApplication {
            application {
                dependencies {
                    provide { mockCalculatorRepository }
                    provide { mockCalculatorService }
                }

                configureTestSecurity()
                configureGraphQL()
            }

            val client =
                createClient {
                    install(ContentNegotiation) { json() }
                }

            response =
                client.post("/graphql") {
                    if (addAuthHeader) bearerAuth(tokenStringInTest)
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(mapOf("query" to query)))
                }
        }

        return response
    }

    companion object {
        val getAllCalculationsQuery =
            """
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
            }
            """.trimIndent()

        val createCalculationMutation =
            """
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
            }
            """.trimIndent()

        val deleteCalculationsMutation =
            """
            mutation {
                deleteCalculations(ids: [1,2])
            }
            """.trimIndent()
    }
}
