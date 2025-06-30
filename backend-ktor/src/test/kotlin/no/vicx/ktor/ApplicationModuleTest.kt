package no.vicx.ktor

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.mockk
import no.vicx.ktor.db.repository.CalculatorRepository

class ApplicationModuleTest :
    BehaviorSpec({
        Given("application context") {
            val mockCalculatorRepository: CalculatorRepository = mockk()

            When("starting the Ktor app, it should not crash") {
                coEvery { mockCalculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(any()) } returns 1

                testApplication {
                    environment {
                        config =
                            MapApplicationConfig(
                                "esport.token" to "~token~",
                                "recaptcha.secret" to "~secret~",
                                "postgres.embedded" to "true",
                                "jwt.issuer" to "http://localhost:9000/auth-server",
                                "jwt.realm" to "~jwt-realm~",
                                "calculator.rate" to "1s",
                                "calculator.max-age" to "10s",
                            )
                    }

                    application {
                        shouldNotThrowAny {
                            module()
                        }
                    }
                }
            }
        }
    })
