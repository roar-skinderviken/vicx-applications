package no.vicx.ktor.health

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row1
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import no.vicx.ktor.plugins.configureHealth
import no.vicx.ktor.util.configureTestDb
import kotlin.time.Duration.Companion.seconds

class HealthRouteTest :
    BehaviorSpec({
        Given("application context with Cohort") {

            forAll(
                Row1("/readiness"),
                Row1("/liveness"),
            ) { endpoint ->
                When("retrieving health status from $endpoint endpoint") {
                    lateinit var response: HttpResponse

                    testApplication {
                        application {
                            configureHealth(configureTestDb())
                        }

                        val httpClient =
                            createClient {
                                install(ContentNegotiation) { json() }
                            }

                        val config =
                            eventuallyConfig {
                                initialDelay = 2.seconds
                                duration = 5.seconds
                            }

                        eventually(config) {
                            response = httpClient.get(endpoint)
                            response.status shouldBe HttpStatusCode.OK
                        }
                    }

                    Then("response should report healthy") {
                        assertSoftly(response) {
                            status shouldBe HttpStatusCode.OK
                            bodyAsText() shouldContain "\"status\":\"Healthy\""
                        }
                    }
                }
            }
        }
    })
