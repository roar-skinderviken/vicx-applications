package no.javatec.calc;

public record CalcVm(
        long firstValue,
        long secondValue,
        String operation,
        Long result
) {
}
