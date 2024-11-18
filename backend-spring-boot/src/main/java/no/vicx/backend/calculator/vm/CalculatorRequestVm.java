package no.vicx.backend.calculator.vm;

import jakarta.validation.constraints.NotNull;
import no.vicx.database.calculator.CalculatorOperation;

public record CalculatorRequestVm(
        @NotNull Long firstValue,
        @NotNull Long secondValue,
        @NotNull CalculatorOperation operation) {
}
