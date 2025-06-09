package no.vicx.ktor.db.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CalcEntry(
    val id: Long = 0L,
    val firstValue: Long,
    val secondValue: Long,
    val operation: CalculatorOperation,
    val result: Long,
    val username: String,
    val createdAt: LocalDateTime = java.time.LocalDateTime.now().toKotlinLocalDateTime()
)
