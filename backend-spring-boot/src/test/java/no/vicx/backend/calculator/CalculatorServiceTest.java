package no.vicx.backend.calculator;

import no.vicx.backend.calculator.repository.CalculatorEntity;
import no.vicx.backend.calculator.repository.CalculatorRepository;
import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorOperation;
import org.junit.jupiter.api.BeforeEach;
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

    @ParameterizedTest
    @MethodSource("provideTestParameters")
    void calculate_bothOperations_savesAndReturnsResult(
            int firstValue, int secondValue, CalculatorOperation operation
    ) {
        // Arrange
        var username = "user1";
        var entityId = 1L;
        long expectedResult = firstValue + secondValue;
        var savedEntity = new CalculatorEntity(firstValue, secondValue, operation, expectedResult, username);
        savedEntity.setId(entityId);

        when(calculatorRepository.save(any(CalculatorEntity.class))).thenReturn(savedEntity);
        when(calculatorRepository.findByIdNotOrderByIdDesc(entityId)).thenReturn(Collections.emptyList());

        // Act
        CalcVm result = sut.calculate(firstValue, secondValue, operation, username);

        // Assert
        assertEquals(expectedResult, result.result());
        verify(calculatorRepository, times(1)).save(any(CalculatorEntity.class));
        verify(calculatorRepository, times(1)).findByIdNotOrderByIdDesc(entityId);
    }

    private static Stream<Arguments> provideTestParameters() {
        return Stream.of(
                Arguments.of(5, 10, CalculatorOperation.PLUS),
                Arguments.of(10, 5, CalculatorOperation.MINUS)
        );
    }
}
