package no.vicx.backend.calculator.vm

import io.swagger.v3.oas.annotations.media.Schema
import no.vicx.database.calculator.CalcEntry
import no.vicx.database.calculator.CalculatorOperation
import java.time.LocalDateTime

/**
 * Represents a detailed view of a calculator operation, including metadata.
 */
@Schema(description = "Represents a detailed view of a calculator operation, including metadata.")
data class CalcVm(
    @Schema(
        description = "The unique identifier of the calculation.",
        example = "12345",
    )
    val id: Int,
    @Schema(
        description = "The first value used in the calculation.",
        example = "10",
    )
    val firstValue: Int,
    @Schema(
        description = "The second value used in the calculation.",
        example = "5",
    )
    val secondValue: Int,
    @Schema(
        description = "The operation performed on the values (e.g., ADD, SUBTRACT).",
        implementation = CalculatorOperation::class,
    )
    val operation: CalculatorOperation,
    @Schema(
        description = "The result of the calculation.",
        example = "15",
    )
    val result: Int,
    @Schema(
        description = "The username of the person who performed the calculation.",
        example = "john_doe",
    )
    val username: String?,
    @Schema(
        description = "The timestamp when the calculation was created.",
        example = "2024-12-07T10:15:30",
    )
    val createdAt: LocalDateTime,
) {
    companion object {
        fun fromEntity(source: CalcEntry) =
            with(source) {
                CalcVm(
                    id = id.toInt(),
                    firstValue = firstValue.toInt(),
                    secondValue = secondValue.toInt(),
                    operation = operation ?: error("operation cannot be null"),
                    result = result.toInt(),
                    username = username,
                    createdAt = createdAt ?: error("createdAt cannot be null"),
                )
            }
    }
}
