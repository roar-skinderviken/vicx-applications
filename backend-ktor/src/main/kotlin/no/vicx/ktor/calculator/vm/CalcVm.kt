package no.vicx.ktor.calculator.vm

import kotlinx.serialization.Serializable
import no.vicx.ktor.db.model.CalculatorOperation

@Serializable
data class CalcVm(
    val id: Int,
    val firstValue: Int,
    val secondValue: Int,
    val operation: CalculatorOperation,
    val result: Int,
    val username: String?,
    val createdAt: String // TODO: Add custom serializer for LocalDateTime
)