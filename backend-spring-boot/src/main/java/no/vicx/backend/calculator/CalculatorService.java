package no.vicx.backend.calculator;

import jakarta.transaction.Transactional;
import no.vicx.backend.calculator.repository.CalculatorEntity;
import no.vicx.backend.calculator.repository.CalculatorRepository;
import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorRequestVm;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CalculatorService {

    private final CalculatorRepository calculatorRepository;

    public CalculatorService(CalculatorRepository calculatorRepository) {
        this.calculatorRepository = calculatorRepository;
    }

    public List<CalcVm> getAllCalculations() {
        return calculatorRepository.findAllByOrderByIdDesc()
                .stream()
                .map(CalcVm::fromEntity)
                .toList();
    }

    public void deleteByIds(List<Long> ids) {
        calculatorRepository.deleteByIdIn(ids);
    }

    public CalcVm calculate(
            CalculatorRequestVm calculatorRequestVm,
            String username) {

        var result = switch (calculatorRequestVm.operation()) {
            case PLUS -> calculatorRequestVm.firstValue() + calculatorRequestVm.secondValue();
            case MINUS -> calculatorRequestVm.firstValue() - calculatorRequestVm.secondValue();
        };

        var savedEntity = calculatorRepository.save(
                new CalculatorEntity(
                        calculatorRequestVm.firstValue(),
                        calculatorRequestVm.secondValue(),
                        calculatorRequestVm.operation(),
                        result,
                        username));

        return CalcVm.fromEntity(savedEntity);
    }
}
