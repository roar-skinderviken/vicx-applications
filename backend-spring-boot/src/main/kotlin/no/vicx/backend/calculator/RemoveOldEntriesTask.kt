package no.vicx.backend.calculator

import no.vicx.backend.loggerFor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class RemoveOldEntriesTask(
    private val calculatorService: CalculatorService
) {
    @Scheduled(fixedRateString = "\${app.calculator.rate}")
    fun removeOldCalculatorEntries() {
        log.info("Removing old calculator entries")
        calculatorService.deleteOldAnonymousCalculations()
    }

    companion object {
        private val log = loggerFor<RemoveOldEntriesTask>()
    }
}
