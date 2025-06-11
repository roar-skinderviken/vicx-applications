package no.vicx.ktor.calculator

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.BehaviorSpec
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.vicx.ktor.db.repository.CalculatorRepository
import kotlin.time.Duration.Companion.seconds

class RemoveOldEntriesTaskTest : BehaviorSpec({
    Given("application context with RemoveOldEntriesTask") {
        val calculatorRepository: CalculatorRepository = mockk()

        When("starting RemoveOldEntriesTask then expect task to be invoked") {
            coEvery { calculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(any()) } returns 1

            testApplication {
                environment {
                    config = MapApplicationConfig(
                        "calculator.rate" to "1s",
                        "calculator.max-age" to "10s"
                    )
                }

                application {
                    val task = RemoveOldEntriesTask(calculatorRepository)
                    val job = task.start(this)

                    try {
                        runBlocking {
                            eventually(5.seconds) {
                                coVerify(atLeast = 1) { calculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(any()) }
                            }
                        }
                    } finally {
                        job.cancel()
                    }
                }
            }
        }
    }
})