package no.vicx.backend.calculator;

public record CalcVm(
        long firstValue,
        long secondValue,
        CalculatorOperation operation,
        long result
) {
}
