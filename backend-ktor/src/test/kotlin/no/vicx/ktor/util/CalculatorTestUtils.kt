package no.vicx.ktor.util

import kotlinx.datetime.toKotlinLocalDateTime
import no.vicx.ktor.db.model.CalcEntry
import no.vicx.ktor.db.model.CalculatorOperation
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import java.time.LocalDateTime

object CalculatorTestUtils {
    fun calcEntryInTest(
        id: Long = 0L,
        username: String? = USERNAME_IN_TEST,
    ) = CalcEntry(
        id,
        42,
        43,
        CalculatorOperation.PLUS,
        85,
        username,
        LocalDateTime.now().plusSeconds(id).toKotlinLocalDateTime(),
    )

    fun generateTestCalcEntries(
        size: Int,
        username: String = USERNAME_IN_TEST,
    ) = List(size) { index ->
        calcEntryInTest(
            id = index.toLong(),
            username = username,
        )
    }
}
