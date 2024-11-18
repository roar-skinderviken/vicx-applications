package no.vicx.backend.calculator;

import no.vicx.database.calculator.CalcEntry;
import no.vicx.database.calculator.CalculatorRepository;
import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorRequestVm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CalculatorService {

    private final CalculatorRepository calculatorRepository;

    public CalculatorService(CalculatorRepository calculatorRepository) {
        this.calculatorRepository = calculatorRepository;
    }

    public Page<CalcVm> getAllCalculations(Pageable pageable) {
        return calculatorRepository.findAllByOrderByIdDesc(pageable)
                .map(CalcVm::fromEntity);
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
                new CalcEntry(
                        calculatorRequestVm.firstValue(),
                        calculatorRequestVm.secondValue(),
                        calculatorRequestVm.operation(),
                        result,
                        username));

        return CalcVm.fromEntity(savedEntity);
    }
}
