package no.vicx.backend.calculator;

import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
class RemoveOldEntriesTaskTest {

    @MockitoBean
    CalculatorService calculatorService;

    @MockitoSpyBean
    RemoveOldEntriesTask removeOldEntriesTask;

    @Test
    void removeOldEntriesTask_expectTimerToFire() {
        await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
            verify(removeOldEntriesTask, atLeast(1)).removeOldCalculatorEntries();
            verify(calculatorService, atLeast(1)).deleteOldAnonymousCalculations();
        });
    }
}