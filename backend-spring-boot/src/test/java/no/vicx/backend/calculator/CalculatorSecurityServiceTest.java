package no.vicx.backend.calculator;

import no.vicx.database.calculator.CalculatorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static no.vicx.backend.testconfiguration.TestSecurityConfig.createJwtInTest;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CalculatorSecurityServiceTest {

    @Mock
    CalculatorRepository calculatorRepository;

    @InjectMocks
    CalculatorSecurityService sut;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);

        when(calculatorRepository.findAllIdsByUsername(anyString()))
                .thenReturn(Set.of(1L));
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void isAllowedToDelete_givenEmptyList_expectTrue() {
        var result = sut.isAllowedToDelete(Collections.emptyList(), TOKEN_IN_TEST);

        assertTrue(result);

        verify(calculatorRepository, never()).findAllIdsByUsername(anyString());
    }

    @Test
    void isAllowedToDelete_givenListWithNull_expectTrue() {
        var result = sut.isAllowedToDelete(Collections.singletonList(null), TOKEN_IN_TEST);

        assertTrue(result);

        verify(calculatorRepository, never()).findAllIdsByUsername(anyString());
    }

    @Test
    void isAllowedToDelete_givenIdsButNoIdsInDatabase_expectFalse() {
        when(calculatorRepository.findAllIdsByUsername(anyString()))
                .thenReturn(Collections.emptySet());

        var result = sut.isAllowedToDelete(List.of(1L), TOKEN_IN_TEST);

        assertFalse(result);
    }

    @Test
    void isAllowedToDelete_givenListOfIdsThatDoesNotBelongToUser_expectFalse() {
        var result = sut.isAllowedToDelete(List.of(1L, 2L), TOKEN_IN_TEST);

        assertFalse(result);

        verify(calculatorRepository).findAllIdsByUsername(anyString());
    }

    @Test
    void isAllowedToDelete_givenListOfIdsThatBelongsToUser_expectTrue() {
        var result = sut.isAllowedToDelete(Collections.singletonList(1L), TOKEN_IN_TEST);

        assertTrue(result);

        verify(calculatorRepository).findAllIdsByUsername(anyString());
    }

    @Test
    void isAllowedToDelete_givenListWithValueAndNull_expectTrue() {
        var result = sut.isAllowedToDelete(Arrays.asList(1L, null), TOKEN_IN_TEST);

        assertTrue(result);

        verify(calculatorRepository).findAllIdsByUsername(anyString());
    }

    static final Authentication TOKEN_IN_TEST =
            new JwtAuthenticationToken(createJwtInTest(Collections.singletonList("USER")));
}