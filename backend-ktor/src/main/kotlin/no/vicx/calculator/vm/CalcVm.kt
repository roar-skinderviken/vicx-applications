package no.vicx.calculator.vm

import kotlinx.serialization.Serializable

@Serializable
data class CalcVm(
    val id: Int,
    val firstValue: Int,
    val secondValue: Int,
    val operation: String,
    val result: Int,
    val username: String,
    val createdAt: String // TODO: Add custom serializer for LocalDateTime
)