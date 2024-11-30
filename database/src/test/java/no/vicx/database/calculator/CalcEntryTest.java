package no.vicx.database.calculator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
}