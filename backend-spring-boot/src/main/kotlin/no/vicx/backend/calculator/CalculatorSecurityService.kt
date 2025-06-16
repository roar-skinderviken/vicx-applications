package no.vicx.backend.calculator

import no.vicx.database.calculator.CalculatorRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service


@Service
class CalculatorSecurityService(
    private val calculatorRepository: CalculatorRepository
) {
    fun isAllowedToDelete(
        idsToDelete: Collection<Long>,
        authentication: Authentication
    ): Boolean {
        if (idsToDelete.isEmpty()) return true // let validation handle this

        val idsFromDatabase = calculatorRepository.findAllIdsByUsername(authentication.name)
        return idsFromDatabase.containsAll(idsToDelete)
    }
}
