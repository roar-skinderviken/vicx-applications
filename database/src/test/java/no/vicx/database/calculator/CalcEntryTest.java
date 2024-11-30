package no.vicx.database.calculator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static no.vicx.database.calculator.CalcEntry.OPERATION_MUST_NOT_BE_NULL;
import static org.junit.jupiter.api.Assertions.*;

class CalcEntryTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void constructor_givenValidValues_expectPopulatedInstance(boolean addUsername) {
        var username = addUsername ? "user1" : null;

        var sut = new CalcEntry(1L, 2L, CalculatorOperation.PLUS, 3L, username);

        assertEquals(1L, sut.getFirstValue());
        assertEquals(2L, sut.getSecondValue());
        assertEquals(CalculatorOperation.PLUS, sut.getOperation());
        assertEquals(3L, sut.getResult());

        if (addUsername) {
            assertEquals("user1", sut.getUsername());
        } else {
            assertNull(sut.getUsername());
        }
    }

    @Test
    void constructor_givenInvalidOperation_expectNullPointerException() {
        var exception = assertThrows(NullPointerException.class, () ->
                new CalcEntry(1L, 2L, null, 3L, null));

        assertEquals(OPERATION_MUST_NOT_BE_NULL, exception.getMessage());
    }

    @Test
    void whenInvokingSetters_expectValuesToBeSet() {
        var sut = new CalcEntry();

        sut.setId(1L);
        assertEquals(1L, sut.getId());

        sut.setFirstValue(1L);
        assertEquals(1L, sut.getFirstValue());

        sut.setSecondValue(2L);
        assertEquals(2L, sut.getSecondValue());

        sut.setOperation(CalculatorOperation.PLUS);
        assertEquals(CalculatorOperation.PLUS, sut.getOperation());

        sut.setResult(3L);
        assertEquals(3L, sut.getResult());

        sut.setUsername("username");
        assertEquals("username", sut.getUsername());

        LocalDateTime dateInTest = LocalDateTime.now();
        sut.setCreatedAt(dateInTest);
        assertEquals(dateInTest, sut.getCreatedAt());
    }
}