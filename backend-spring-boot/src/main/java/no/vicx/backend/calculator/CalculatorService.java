package no.vicx.backend.calculator;

import no.vicx.database.calculator.CalcEntry;
import no.vicx.database.calculator.CalculatorRepository;
import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorRequestVm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CalculatorService {

    private final CalculatorRepository calculatorRepository;
    private final Duration maxAge;

    public CalculatorService(
            CalculatorRepository calculatorRepository,
            @Value("${app.calculator.max-age}") Duration maxAge) {
        this.calculatorRepository = calculatorRepository;
        this.maxAge = maxAge;
    }

    public Page<CalcVm> getAllCalculations(Integer page) {
        return calculatorRepository.findAllBy(
                        PageRequest.of(page != null ? page : 0,
                                10,
                                Sort.by("id").descending()))
                .map(CalcVm::fromEntity);
    }

    public void deleteByIds(List<Long> ids) {
        calculatorRepository.deleteByIdIn(ids);
    }

    public void deleteOldAnonymousCalculations() {
        calculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(
                LocalDateTime.now().minus(maxAge));
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
