package no.vicx.backend.calculator.vm;

import no.vicx.database.calculator.CalcEntry;
import no.vicx.database.calculator.CalculatorOperation;

import java.time.LocalDateTime;

public record CalcVm(
        long id,
        long firstValue,
        long secondValue,
        CalculatorOperation operation,
        long result,
        String username,
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
