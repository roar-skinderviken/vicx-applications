package no.vicx.backend.calculator;

import no.vicx.database.calculator.CalcEntry;
import no.vicx.database.calculator.CalculatorRepository;
import no.vicx.database.calculator.CalculatorOperation;
import no.vicx.backend.calculator.vm.CalculatorRequestVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CalculatorServiceTest {

    @Mock
    CalculatorRepository calculatorRepository;

    @InjectMocks
    CalculatorService sut;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void deleteByIds_givenNonEmptyList_expectRepositoryToBeInvoked() {
        var idsToDelete = Collections.singletonList(1L);

        sut.deleteByIds(idsToDelete);

        verify(calculatorRepository, times(1)).deleteByIdIn(idsToDelete);
    }

    @ParameterizedTest
    @MethodSource("provideTestParameters")
    void calculate_bothOperations_savesAndReturnsResult(
            Long firstValue, Long secondValue, CalculatorOperation operation
    ) {
        // Arrange
        var username = "user1";
        var entityId = 1L;
        long expectedResult = firstValue + secondValue;
        var savedEntity = new CalcEntry(firstValue, secondValue, operation, expectedResult, username);
        savedEntity.setId(entityId);

        when(calculatorRepository.save(any(CalcEntry.class))).thenReturn(savedEntity);

        var requestBody = new CalculatorRequestVm(
                firstValue,
                secondValue,
                operation);

        // Act
        var calcVm = sut.calculate(requestBody, username);

        // Assert
        assertEquals(expectedResult, calcVm.result());
        verify(calculatorRepository, times(1)).save(any(CalcEntry.class));
    }

    private static Stream<Arguments> provideTestParameters() {
        return Stream.of(
                Arguments.of(5L, 10L, CalculatorOperation.PLUS),
                Arguments.of(10L, 5L, CalculatorOperation.MINUS)
        );
    }
}
