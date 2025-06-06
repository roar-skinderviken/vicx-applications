package no.vicx.routing

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import no.vicx.calculator.CalculatorService
import no.vicx.calculator.toGraphQLModel
import no.vicx.calculator.vm.PaginatedCalculations
import no.vicx.plugins.configureSerialization
import no.vicx.util.CalculatorTestUtils.generateTestCalcEntries
import no.vicx.util.SecurityTestUtils.configureTestSecurity
import no.vicx.util.SecurityTestUtils.tokenStringInTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

class RouteTest {

    @Test
    fun someTest() = testApplication {
        val calculatorService: CalculatorService = mockk()

        val mockResult = PaginatedCalculations(
            calculations = generateTestCalcEntries(1).map { it.toGraphQLModel() },
            page = 1,
            totalPages = 1,
        )

        coEvery { calculatorService.getPagedCalculations(1) } returns mockResult

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        application {
            install(ContentNegotiation) { json() }
            configureTestSecurity()
            configureSerialization(calculatorService, mockk())
        }

        val response = client.get("/calculations/get-all/1") {
            bearerAuth(tokenStringInTest)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.body<PaginatedCalculations>()
        assertEquals(mockResult, responseBody)
    }
}
