package no.vicx.backend.calculator.vm;

import java.util.List;

public record PaginatedCalculations(
        List<CalcVm> calculations,
        int page,
        int totalPages) {
}
