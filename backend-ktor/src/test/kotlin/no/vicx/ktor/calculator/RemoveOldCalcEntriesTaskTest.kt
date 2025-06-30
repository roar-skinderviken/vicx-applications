package no.vicx.ktor.calculator

import ch.qos.logback.classic.spi.ILoggingEvent
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.vicx.ktor.db.repository.CalculatorRepository
import no.vicx.ktor.plugins.configureOldCalcEntryCleanup
import no.vicx.ktor.util.withLogCapture
import kotlin.time.Duration.Companion.seconds

class RemoveOldCalcEntriesTaskTest :
    BehaviorSpec({
        Given("application context with RemoveOldEntriesTask") {
            val mockCalculatorRepository: CalculatorRepository = mockk()

            forAll(
                Row3("it should log success message", false, "Removed old calculator entries older than"),
                Row3("it should log warning message", true, "Failed to remove old entries"),
            ) { description, throwException, expectedLogMessage ->
                When(description) {
                    if (throwException) {
                        coEvery { mockCalculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(any()) } throws RuntimeException()
                    } else {
                        coEvery { mockCalculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(any()) } returns 1
                    }

                    runInTestApplication(mockCalculatorRepository) { logEvents ->
                        eventually(5.seconds) {
                            val messages = logEvents.map { it.formattedMessage }
                            messages.shouldNotBeEmpty()
                            messages.any { it.contains(expectedLogMessage) } shouldBe true

                            coVerify(atLeast = 1) {
                                mockCalculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(
                                    any(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }) {
    companion object {
        fun runInTestApplication(
            calculatorRepository: CalculatorRepository,
            block: suspend (List<ILoggingEvent>) -> Unit,
        ) {
            testApplication {
                environment {
                    config =
                        MapApplicationConfig(
                            "calculator.rate" to "1s",
                            "calculator.max-age" to "10s",
                        )
                }

                application {
                    dependencies {
                        provide { calculatorRepository }
                    }

                    withLogCapture("RemoveOldCalcEntriesTask") { logEvents ->
                        configureOldCalcEntryCleanup()
                        block(logEvents)
                    }
                }
            }
        }
    }
}
