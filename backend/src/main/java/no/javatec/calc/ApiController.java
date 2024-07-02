package no.javatec.calc;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {

    // URL for testing localhost: http://localhost:8080/api/5/10/plus
    // URL for testing: https://vicx.no/sample/api/5/10/plus

    private final CalculatorService calculatorService;

    public ApiController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @RequestMapping(
            value = "/api/{firstValue}/{secondValue}/{operation}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CalcVm index(
            @PathVariable long firstValue,
            @PathVariable long secondValue,
            @PathVariable String operation) {

        return calculatorService.calculate(firstValue, secondValue, operation);
    }
}
