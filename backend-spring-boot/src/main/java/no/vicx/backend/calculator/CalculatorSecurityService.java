package no.vicx.backend.calculator;

import no.vicx.backend.calculator.repository.CalculatorRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculatorSecurityService {

    private final CalculatorRepository calculatorRepository;

    public CalculatorSecurityService(CalculatorRepository calculatorRepository) {
        this.calculatorRepository = calculatorRepository;
    }

    public boolean isAllowedToDelete(final List<Long> ids, JwtAuthenticationToken authentication) {
        var idsFromDatabase = calculatorRepository.findAllIdsByUsername(authentication.getName());
        return idsFromDatabase.containsAll(ids);
    }
}
