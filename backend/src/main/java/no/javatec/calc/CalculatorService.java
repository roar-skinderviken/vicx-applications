package no.javatec.calc;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    public CalcVm calculate(
            long firstValue, long secondValue, CalculatorOperation operation, String message) {
        var result = switch (operation) {
            case PLUS -> firstValue + secondValue;
            case MINUS -> firstValue - secondValue;
        };

        return new CalcVm(firstValue, secondValue, operation, result, message);
    }
}
