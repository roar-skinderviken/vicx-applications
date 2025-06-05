package no.vicx.calculator.query

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import no.vicx.calculator.CalculatorService
import no.vicx.calculator.query.wrapper.GraphQLResponseBody
import no.vicx.db.repository.CalculatorRepository
import no.vicx.plugins.graphQLModule
import no.vicx.util.SecurityTestUtils.configureTestSecurity
import no.vicx.util.SecurityTestUtils.tokenStringInTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach

abstract class CalculatorGraphQLTestBase {
    lateinit var calculatorService: CalculatorService
    lateinit var calculatorRepository: CalculatorRepository

    @BeforeEach
    fun setup() {
        clearAllMocks()
        calculatorService = mockk()
        calculatorRepository = mockk()
    }

    protected inline fun <reified T> postGraphQLRequestAndReturnResult(
        query: String,
        addAuthHeader: Boolean = false,
    ): GraphQLResponseBody<T> {
        val response = postGraphQLRequest(query, addAuthHeader)
        assertEquals(HttpStatusCode.OK, response.status)
        return runBlocking { response.body<GraphQLResponseBody<T>>() }
    }

    protected fun postGraphQLRequest(
        query: String,
        addAuthHeader: Boolean = false,
    ): HttpResponse {
        var response: HttpResponse? = null

        testApplication {
            application {
                configureTestSecurity()
                graphQLModule(calculatorService, calculatorRepository)
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

        return response ?: throw IllegalStateException("Test application did not produce a result")
    }
}