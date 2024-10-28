package no.javatec.calc;

public record CalcVm(
        long firstValue,
        long secondValue,
        CalculatorOperation operation,
        long result
) {
}
