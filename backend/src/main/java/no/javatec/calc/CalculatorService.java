package no.javatec.calc;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    public CalcVm calculate(long firstValue, long secondValue, String operation) {
        Long result = switch (operation) {
            case "plus" -> firstValue + secondValue;
            case "minus" -> firstValue - secondValue;
            default -> null;
        };

        return new CalcVm(firstValue, secondValue, operation, result);
    }
}
