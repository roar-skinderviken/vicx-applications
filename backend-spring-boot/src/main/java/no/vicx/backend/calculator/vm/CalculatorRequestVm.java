package no.vicx.backend.calculator.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import no.vicx.database.calculator.CalculatorOperation;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * A request object representing a calculator operation with two values and an operator.
 */
@Schema(description = "Represents a request for a calculator operation.")
public record CalculatorRequestVm(
        @Schema(description = "The first value in the calculation.",
                example = "10",
                requiredMode = REQUIRED)
        @NotNull Long firstValue,

        @Schema(description = "The second value in the calculation.",
                example = "5",
                requiredMode = REQUIRED)
        @NotNull Long secondValue,

        @Schema(description = "The operation to perform on the values (e.g., ADD, SUBTRACT).",
                implementation = CalculatorOperation.class,
                requiredMode = REQUIRED)
        @NotNull CalculatorOperation operation
) {
}
