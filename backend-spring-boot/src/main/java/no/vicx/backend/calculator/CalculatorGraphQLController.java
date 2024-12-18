package no.vicx.backend.calculator;

import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorRequestVm;
import no.vicx.backend.calculator.vm.PaginatedCalculations;
import no.vicx.database.calculator.CalculatorOperation;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class CalculatorGraphQLController {
    private final CalculatorService calculatorService;

    public CalculatorGraphQLController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @QueryMapping
    public PaginatedCalculations getAllCalculations(@Argument int page) {
        var result = calculatorService.getAllCalculations(page);
        return new PaginatedCalculations(
                result.getContent(),
                result.getNumber(),
                result.getTotalPages());
    }

    @MutationMapping
    public CalcVm createCalculation(
            @Argument long firstValue,
            @Argument long secondValue,
            @Argument CalculatorOperation operation,
            Authentication authentication) {

        return calculatorService.calculate(
                new CalculatorRequestVm(firstValue, secondValue, operation),
                Optional.ofNullable(authentication)
                        .map(Authentication::getName)
                        .orElse(null)
        );
    }

    @MutationMapping
    @PreAuthorize("(hasRole('USER') or hasRole('GITHUB_USER')) and @calculatorSecurityService.isAllowedToDelete(#ids, authentication)")
    public Boolean deleteCalculations(@Argument List<Long> ids) {
        calculatorService.deleteByIds(ids);
        return true;
    }
}
