package no.vicx.backend.calculator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorRequestVm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/calculator")
@RestController
@Validated
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@calculatorSecurityService.isAllowedToDelete(#ids, authentication)")
    public ResponseEntity<Void> deleteByIds(
            @RequestBody @NotEmpty List<@NotNull Long> ids) {
        calculatorService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CalcVm calculateAndReturnResult(
            @Valid @RequestBody CalculatorRequestVm request,
            Authentication authentication) {
        return calculatorService.calculate(
                request,
                Optional.ofNullable(authentication)
                        .map(Authentication::getName)
                        .orElse(null)
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<CalcVm> index(Pageable pageable) {
        return calculatorService.getAllCalculations(pageable);
    }
}
