package no.vicx.backend.calculator;

import no.vicx.backend.testconfiguration.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static no.vicx.backend.jwt.JwtConstants.BEARER_PREFIX;
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
        var response = sendDelete(false, Collections.singletonList(1L));

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

    static Stream<Arguments> badRequestBodies() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of(Collections.emptyList(), true),
                Arguments.of(Collections.singletonList(null), true)
        );
    }

    @ParameterizedTest
    @MethodSource("badRequestBodies")
    void delete_expectBadRequest(Collection<Long> ids) {
        when(calculatorSecurityService.isAllowedToDelete(anyList(), any()))
                .thenReturn(true);

        var response = sendDelete(true, ids);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(calculatorService, never()).deleteByIds(anyList());
    }

    @Test
    void delete_givenEmptyList_expectBadRequest() {
        when(calculatorSecurityService.isAllowedToDelete(anyList(), any()))
                .thenReturn(true);

        var response = sendDelete(true, Collections.emptyList());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(calculatorService, never()).deleteByIds(anyList());
    }

    @Test
    void delete_givenListOfNull_expectBadRequest() {
        when(calculatorSecurityService.isAllowedToDelete(anyList(), any()))
                .thenReturn(true);

        var response = sendDelete(true, Collections.singletonList(null));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(calculatorService, never()).deleteByIds(anyList());
    }

    ResponseEntity<Void> sendDelete() {
        return sendDelete(true, Collections.singletonList(1L));
    }

    ResponseEntity<Void> sendDelete(
            boolean isAuthenticated, Collection<Long> ids) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (isAuthenticated) {
            headers.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + "token");
        }

        var httpEntity = new HttpEntity<>(ids, headers);

        return restTemplate.exchange(
                "/api/calculator",
                HttpMethod.DELETE,
                httpEntity,
                Void.class);
    }
}
