package no.vicx.backend.calculator;

import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorOperation;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CalculatorController {
    // URL for testing on localhost: http://localhost:8080/api/calculator/5/10/PLUS
    private static final String CALC_TEMPLATE = "/calculator/{firstValue}/{secondValue}/{operation}";
    static final String URL_TEMPLATE = "/api" + CALC_TEMPLATE;
    static final String SECURED_URL_TEMPLATE = "/api-secured" + CALC_TEMPLATE;

    private final CalculatorService calculatorService;

    public CalculatorController(
            CalculatorService calculatorService
    ) {
        this.calculatorService = calculatorService;
    }

    @RequestMapping(
            value = {URL_TEMPLATE, SECURED_URL_TEMPLATE},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CalcVm index(
            @PathVariable long firstValue,
            @PathVariable long secondValue,
            @PathVariable CalculatorOperation operation,
            JwtAuthenticationToken token
    ) {
        return calculatorService.calculate(
                firstValue,
                secondValue,
                operation,
                Optional.ofNullable(token)
                        .map(JwtAuthenticationToken::getName)
                        .orElse(null)
        );
    }
}
