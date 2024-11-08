package no.vicx.backend.calculator;

import jakarta.transaction.Transactional;
import no.vicx.backend.calculator.repository.CalculatorEntity;
import no.vicx.backend.calculator.repository.CalculatorRepository;
import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorOperation;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CalculatorService {

    private final CalculatorRepository calculatorRepository;

    public CalculatorService(CalculatorRepository calculatorRepository) {
        this.calculatorRepository = calculatorRepository;
    }

    public CalcVm calculate(
            long firstValue,
            long secondValue,
            CalculatorOperation operation,
            String username) {

        var result = switch (operation) {
            case PLUS -> firstValue + secondValue;
            case MINUS -> firstValue - secondValue;
        };

        var savedEntity = calculatorRepository.save(
                new CalculatorEntity(
                        firstValue,
                        secondValue,
                        operation,
                        result,
                        username));

        var historicalCalculation =
                calculatorRepository.findByIdNotOrderByIdDesc(savedEntity.getId());

        return CalcVm.fromEntity(savedEntity, historicalCalculation);
    }
}
