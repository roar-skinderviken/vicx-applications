package no.vicx.backend.calculator.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import no.vicx.database.calculator.CalcEntry;
import no.vicx.database.calculator.CalculatorOperation;

import java.time.LocalDateTime;

/**
 * Represents a detailed view of a calculator operation, including metadata.
 */
@Schema(description = "Represents a detailed view of a calculator operation, including metadata.")
public record CalcVm(
        @Schema(description = "The unique identifier of the calculation.", example = "12345")
        long id,

        @Schema(description = "The first value used in the calculation.", example = "10")
        long firstValue,

        @Schema(description = "The second value used in the calculation.", example = "5")
        long secondValue,

        @Schema(description = "The operation performed on the values (e.g., ADD, SUBTRACT).",
                implementation = CalculatorOperation.class)
        CalculatorOperation operation,

        @Schema(description = "The result of the calculation.", example = "15")
        long result,

        @Schema(description = "The username of the person who performed the calculation.",
                example = "john_doe")
        String username,

        @Schema(description = "The timestamp when the calculation was created.",
                example = "2024-12-07T10:15:30")
        LocalDateTime createdAt
) {
    public static CalcVm fromEntity(CalcEntry entity) {
        return new CalcVm(
                entity.getId(),
                entity.getFirstValue(),
                entity.getSecondValue(),
                entity.getOperation(),
                entity.getResult(),
                entity.getUsername(),
                entity.getCreatedAt()
        );
    }
}
