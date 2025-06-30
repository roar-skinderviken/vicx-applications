package no.vicx.ktor.plugins

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import no.vicx.ktor.db.repository.CalculatorRepository
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import kotlin.time.Duration
import kotlin.time.toJavaDuration

fun Application.configureOldCalcEntryCleanup() {
    val log = LoggerFactory.getLogger("RemoveOldCalcEntriesTask")
    val rate: Duration =
        Duration.parse(
            environment.config
                .property("calculator.rate")
                .getString(),
        )
    val maxAge: Duration =
        Duration.parse(
            environment.config
                .property("calculator.max-age")
                .getString(),
        )

    launch {
        val calculatorRepository: CalculatorRepository = dependencies.resolve()

        while (isActive) {
            delay(rate)

            runCatching {
                val createdBefore = OffsetDateTime.now().minus(maxAge.toJavaDuration())
                calculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(createdBefore)
                log.info("Removed old calculator entries older than {}", createdBefore)
            }.onFailure { exception ->
                log.warn("Failed to remove old entries", exception)
            }
        }
    }
}
