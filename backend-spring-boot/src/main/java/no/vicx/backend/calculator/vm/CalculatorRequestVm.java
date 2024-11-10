package no.vicx.backend.calculator.vm;

import jakarta.validation.constraints.NotNull;

public record CalculatorRequestVm(
        @NotNull Long firstValue,
        @NotNull Long secondValue,
        @NotNull CalculatorOperation operation) {
}
