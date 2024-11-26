package no.vicx.backend.calculator;

import no.vicx.database.calculator.CalculatorRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public record CalculatorSecurityService(CalculatorRepository calculatorRepository) {

    public boolean isAllowedToDelete(
            @NonNull Collection<Long> ids,
            @NonNull JwtAuthenticationToken authentication) {
        if (ids.isEmpty()) {
            return true; // let validation handle this
        }

        var nonNullIds = ids.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (nonNullIds.isEmpty()) {
            return true; // let validation handle this
        }

        var idsFromDatabase = calculatorRepository.findAllIdsByUsername(authentication.getName());
        return idsFromDatabase.containsAll(nonNullIds);
    }
}
