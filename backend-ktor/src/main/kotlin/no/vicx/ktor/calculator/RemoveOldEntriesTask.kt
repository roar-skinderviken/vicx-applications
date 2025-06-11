package no.vicx.ktor.calculator

import io.ktor.server.application.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import no.vicx.ktor.db.repository.CalculatorRepository
import no.vicx.ktor.loggerFor
import java.time.OffsetDateTime
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class RemoveOldEntriesTask(
    private val calculatorRepository: CalculatorRepository
) {
    fun start(app: Application): Job = app.launch {
        val rate: Duration = Duration.parse(app.environment.config.property("calculator.rate").getString())
        val maxAge: Duration = Duration.parse(app.environment.config.property("calculator.max-age").getString())

        while (isActive) {
            delay(rate)

            runCatching {
                log.info("Removing old calculator entries")
                val createdBefore = OffsetDateTime.now().minus(maxAge.toJavaDuration())
                calculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(createdBefore)
            }.onFailure { exception ->
                log.warn("Failed to remove old entries", exception)
            }
        }
    }

    companion object {
        private val log = loggerFor<RemoveOldEntriesTask>()
    }
}