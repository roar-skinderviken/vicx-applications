package no.vicx.backend.calculator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorRequestVm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Calculator", description = "API for performing calculations and managing history")
@RequestMapping("/api/calculator")
@RestController
@Validated
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @SecurityRequirement(name = "security_auth")
    @Operation(
            summary = "Delete calculations by IDs",
            description = "Deletes calculations identified by a list of IDs. Requires authorization.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the calculations."),
                    @ApiResponse(responseCode = "403", description = "Access denied."),
                    @ApiResponse(responseCode = "400", description = "Invalid request.")})
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@calculatorSecurityService.isAllowedToDelete(#ids, authentication)")
    public ResponseEntity<Void> deleteByIds(
            @RequestBody @NotEmpty List<@NotNull Long> ids) {
        calculatorService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "security_auth")
    @Operation(
            summary = "Perform a calculation",
            description = "Processes the provided calculation request and returns the result.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Calculation performed successfully.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CalcVm.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid calculation request.")})
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CalcVm calculateAndReturnResult(
            @Valid @RequestBody CalculatorRequestVm request,
            @Parameter(hidden = true) Authentication authentication) {
        return calculatorService.calculate(
                request,
                Optional.ofNullable(authentication)
                        .map(Authentication::getName)
                        .orElse(null)
        );
    }

    @Operation(
            summary = "List all calculations",
            description = "Retrieves a paginated list of all calculations.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the calculations.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CalcVm.class))))})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<CalcVm> index(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", required = false) Integer page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", required = false) Integer size,
            @Parameter(description = "Sort criteria")
            @RequestParam(value = "sort", required = false) String sort) {
        Pageable pageable = PageRequest.of(page != null ? page : 0,
                size != null ? size : 10,
                Sort.by(sort != null ? sort : "id").ascending());
        return calculatorService.getAllCalculations(pageable);
    }
}
