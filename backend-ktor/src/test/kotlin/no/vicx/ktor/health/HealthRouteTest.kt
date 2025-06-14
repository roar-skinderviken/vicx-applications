package no.vicx.ktor.health

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import no.vicx.ktor.plugins.configureHealth
import no.vicx.ktor.util.configureTestDb
import kotlin.time.Duration.Companion.seconds

class HealthRouteTest : BehaviorSpec({
    Given("application context with Cohort") {

        forAll(
            row("/readiness"),
            row("/liveness")
        ) { endpoint ->
            When("calling endpoint $endpoint") {
                lateinit var response: HttpResponse

                testApplication {
                    application {
                        configureHealth(configureTestDb())
                    }

                    val httpClient = createClient {
                        install(ContentNegotiation) { json() }
                    }

                    val config = eventuallyConfig {
                        initialDelay = 2.seconds
                        duration = 5.seconds
                    }

                    eventually(config) {
                        response = httpClient.get(endpoint)
                        response.status shouldBe HttpStatusCode.OK
                    }
                }

                Then("response should report healthy") {
                    response.status shouldBe HttpStatusCode.OK

                    response.bodyAsText() shouldContain "\"status\":\"Healthy\""
                }
            }
        }
    }
})