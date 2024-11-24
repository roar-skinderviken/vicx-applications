package no.vicx.backend.calculator;

import no.vicx.backend.testconfiguration.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
class CalculatorControllerDeleteTest {

    @Autowired
    TestRestTemplate restTemplate;

    @MockitoBean
    CalculatorSecurityService calculatorSecurityService;

    @MockitoBean
    CalculatorService calculatorService;

    @Test
    void delete_givenNotAuthenticated_expectUnauthorized() {
        var response = sendDelete(false);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(calculatorSecurityService, never()).isAllowedToDelete(anyList(), any());
        verify(calculatorService, never()).deleteByIds(anyList());
    }

    @Test
    void delete_givenNotIsAllowedToDelete_expectForbidden() {
        when(calculatorSecurityService.isAllowedToDelete(anyList(), any()))
                .thenReturn(false);

        var response = sendDelete();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        verify(calculatorSecurityService, times(1)).isAllowedToDelete(anyList(), any());
        verify(calculatorService, never()).deleteByIds(anyList());
    }

    @Test
    void delete_givenIsAllowedToDelete_expectOK() {
        when(calculatorSecurityService.isAllowedToDelete(anyList(), any()))
                .thenReturn(true);

        var response = sendDelete();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(calculatorService, times(1)).deleteByIds(anyList());
    }

    ResponseEntity<Void> sendDelete() {
        return sendDelete(true);
    }

    ResponseEntity<Void> sendDelete(boolean isAuthenticated) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (isAuthenticated) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer token");
        }

        var httpEntity = new HttpEntity<>(Collections.singletonList(1L), headers);

        return restTemplate.exchange(
                "/api/calculator",
                HttpMethod.DELETE,
                httpEntity,
                Void.class);
    }
}
