package no.vicx.backend.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public record RemoveOldEntriesTask(CalculatorService calculatorService) {
    private static final Logger LOG = LoggerFactory.getLogger(RemoveOldEntriesTask.class);

    @Scheduled(fixedRateString = "${app.calculator.rate}")
    public void removeOldCalculatorEntries() {
        LOG.info("Removing old calculator entries");
        calculatorService.deleteOldAnonymousCalculations();
    }
}
