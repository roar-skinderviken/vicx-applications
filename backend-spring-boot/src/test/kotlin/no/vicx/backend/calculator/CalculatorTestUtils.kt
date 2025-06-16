package no.vicx.backend.calculator

import no.vicx.database.calculator.CalcEntry
import no.vicx.database.calculator.CalculatorOperation
import java.time.LocalDateTime

object CalculatorTestUtils {

    fun createCalcEntryInTest(
        addOperation: Boolean = true,
        addCreatedAt: Boolean = true
    ) = CalcEntry().apply {
        id = 42L
        firstValue = 1L
        secondValue = 2L
        if (addOperation) operation = CalculatorOperation.PLUS
        result = 3L
        username = "user1"
        if (addCreatedAt) createdAt = LocalDateTime.now()
    }
}