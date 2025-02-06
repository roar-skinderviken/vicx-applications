package no.vicx.backend.calculator;

import no.vicx.database.calculator.CalcEntry;
import no.vicx.database.calculator.CalculatorOperation;
import no.vicx.database.calculator.CalculatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Duration;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceTest {

    @Mock
    CalculatorRepository calculatorRepository;

    CalculatorService sut;

    @BeforeEach
    void setUp() {
        sut = new CalculatorService(calculatorRepository, Duration.ZERO);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getAllCalculations_givenDataInDatabase_expectPageWithCalculations(boolean addPage) {
        var calcEntry = new CalcEntry();
        calcEntry.setId(1L);

        var calcEntries = Collections.singletonList(calcEntry);

        var page = new PageImpl<>(
                calcEntries,
                PageRequest.of(0, 10, Sort.Direction.DESC, "id"),
                calcEntries.size());

        var pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        when(calculatorRepository.findAllBy(pageable)).thenReturn(page);

        var result = sut.getAllCalculations(addPage ? 0 : null);

        assertNotNull(result);

        verify(calculatorRepository).findAllBy(pageable);
    }

    @Test
    void deleteByIds_givenNonEmptyList_expectRepositoryToBeInvoked() {
        var idsToDelete = Collections.singletonList(1L);

        sut.deleteByIds(idsToDelete);

        verify(calculatorRepository).deleteByIdIn(idsToDelete);
    }

    @Test
    void deleteOldAnonymousCalculations_expectRepositoryToBeInvoked() {
        sut.deleteOldAnonymousCalculations();

        verify(calculatorRepository).deleteAllByCreatedAtBeforeAndUsernameNull(any());
    }

    @ParameterizedTest
    @MethodSource("provideTestParameters")
    void calculate_bothOperations_savesAndReturnsResult(
            long firstValue, long secondValue, CalculatorOperation operation) {

        var username = "user1";
        long expectedResult = firstValue + secondValue;
        var savedEntity = new CalcEntry(firstValue, secondValue, operation, expectedResult, username);
        savedEntity.setId(1L);

        when(calculatorRepository.save(any(CalcEntry.class))).thenReturn(savedEntity);

        var calcVm = sut.calculate(firstValue, secondValue, operation, username);

        assertEquals(expectedResult, calcVm.result());
        verify(calculatorRepository).save(any(CalcEntry.class));
    }

    private static Stream<Arguments> provideTestParameters() {
        return Stream.of(
                Arguments.of(5L, 10L, CalculatorOperation.PLUS),
                Arguments.of(10L, 5L, CalculatorOperation.MINUS)
        );
    }
}
