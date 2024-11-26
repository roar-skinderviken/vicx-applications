package no.vicx.backend.calculator;

import no.vicx.backend.calculator.vm.CalculatorRequestVm;
import no.vicx.database.calculator.CalcEntry;
import no.vicx.database.calculator.CalculatorOperation;
import no.vicx.database.calculator.CalculatorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CalculatorServiceTest {

    @Mock
    CalculatorRepository calculatorRepository;

    CalculatorService sut;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);
        sut = new CalculatorService(calculatorRepository, Duration.ZERO);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void getAllCalculations_givenDataInDatabase_expectPageWithCalculations() {
        var calcEntry = new CalcEntry();
        calcEntry.setId(1L);

        var calcEntries = Collections.singletonList(calcEntry);

        var page = new PageImpl<>(
                calcEntries,
                PageRequest.of(0, 10),
                calcEntries.size());

        var pageable = Pageable.ofSize(10);

        when(calculatorRepository.findAllByOrderByIdDesc(pageable)).thenReturn(page);

        var result = sut.getAllCalculations(pageable);

        assertNotNull(result);

        verify(calculatorRepository).findAllByOrderByIdDesc(pageable);
    }

    @Test
    void deleteByIds_givenNonEmptyList_expectRepositoryToBeInvoked() {
        var idsToDelete = Collections.singletonList(1L);

        sut.deleteByIds(idsToDelete);

        verify(calculatorRepository, times(1)).deleteByIdIn(idsToDelete);
    }

    @Test
    void deleteOldAnonymousCalculations_expectRepositoryToBeInvoked() {
        sut.deleteOldAnonymousCalculations();

        verify(calculatorRepository).deleteAllByCreatedAtBeforeAndUsernameNull(any());
    }

    @ParameterizedTest
    @MethodSource("provideTestParameters")
    void calculate_bothOperations_savesAndReturnsResult(
            Long firstValue, Long secondValue, CalculatorOperation operation) {
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
