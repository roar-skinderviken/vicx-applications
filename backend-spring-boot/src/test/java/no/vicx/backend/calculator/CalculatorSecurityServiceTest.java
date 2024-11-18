package no.vicx.backend.calculator;

import no.vicx.database.calculator.CalculatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static no.vicx.backend.testconfiguration.TestSecurityConfig.JWT_IN_TEST;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CalculatorSecurityServiceTest {

    @Mock
    CalculatorRepository calculatorRepository;

    @InjectMocks
    CalculatorSecurityService sut;

    @BeforeEach
    void setUp() {
        openMocks(this);

        when(calculatorRepository.findAllIdsByUsername(anyString()))
                .thenReturn(Set.of(1L));
    }

    @Test
    void isAllowedToDelete_givenListOfIdsThatDoesNotBelongToUser_expectFalse() {
        var result = sut.isAllowedToDelete(List.of(1L, 2L), TOKEN_IN_TEST);

        assertFalse(result);

        verify(calculatorRepository, times(1)).findAllIdsByUsername(anyString());
    }

    @Test
    void isAllowedToDelete_givenListOfIdsThatBelongsToUser_expectTrue() {
        var result = sut.isAllowedToDelete(Collections.singletonList(1L), TOKEN_IN_TEST);

        assertTrue(result);

        verify(calculatorRepository, times(1)).findAllIdsByUsername(anyString());
    }

    static final JwtAuthenticationToken TOKEN_IN_TEST = new JwtAuthenticationToken(JWT_IN_TEST);
}