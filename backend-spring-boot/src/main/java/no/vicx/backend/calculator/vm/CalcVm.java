package no.vicx.backend.calculator.vm;

import no.vicx.backend.calculator.repository.CalculatorEntity;

import java.time.LocalDateTime;
import java.util.Collection;

public record CalcVm(
        long firstValue,
        long secondValue,
        CalculatorOperation operation,
        long result,
        String username,
        LocalDateTime createdAt,
        Collection<CalcVm> previousResults
) {
    public static CalcVm fromEntity(
            CalculatorEntity entity,
            Collection<CalculatorEntity> previousResults) {
        return new CalcVm(
                entity.getFirstValue(),
                entity.getSecondValue(),
                entity.getOperation(),
                entity.getResult(),
                entity.getUsername(),
                entity.getCreatedAt(),
                previousResults
                        .stream()
                        .map(CalcVm::apply)
                        .toList()
        );
    }

    private static CalcVm apply(CalculatorEntity it) {
        return new CalcVm(
                it.getFirstValue(),
                it.getSecondValue(),
                it.getOperation(),
                it.getResult(),
                it.getUsername(),
                it.getCreatedAt(),
                null);
    }
}
