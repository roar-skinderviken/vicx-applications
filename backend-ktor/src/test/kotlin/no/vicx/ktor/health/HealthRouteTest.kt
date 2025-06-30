package no.vicx.ktor.health

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import no.vicx.ktor.plugins.configureHealth
import no.vicx.ktor.util.configureTestDb
import kotlin.time.Duration.Companion.seconds

class HealthRouteTest :
    BehaviorSpec({
        Given("application context with Cohort") {
            When("retrieving health status from liveness and readiness") {
                lateinit var livenessResponse: HttpResponse
                lateinit var readinessResponse: HttpResponse

                testApplication {
                    application {
                        dependencies {
                            provide { configureTestDb() }
                        }

                        configureHealth()
                    }

                    val httpClient =
                        createClient {
                            install(ContentNegotiation) { json() }
                        }

                    eventually(
                        eventuallyConfig {
                            initialDelay = 2.seconds
                            duration = 5.seconds
                        },
                    ) {
                        livenessResponse = httpClient.get("/liveness")
                        livenessResponse.status shouldBe HttpStatusCode.OK

                        readinessResponse = httpClient.get("/readiness")
                        readinessResponse.status shouldBe HttpStatusCode.OK
                    }
                }

                Then("it should report healthy for both endpoints") {
                    assertSoftly(livenessResponse) {
                        status shouldBe HttpStatusCode.OK
                        bodyAsText() shouldContain "\"status\":\"Healthy\""
                    }

                    assertSoftly(readinessResponse) {
                        status shouldBe HttpStatusCode.OK
                        bodyAsText() shouldContain "\"status\":\"Healthy\""
                    }
                }
            }
        }
    })
