package no.vicx.backend.calculator

import no.vicx.backend.calculator.vm.CalcVm
import no.vicx.backend.calculator.vm.PaginatedCalculations
import no.vicx.database.calculator.CalculatorOperation
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller

@Controller
class CalculatorGraphQLController(
    private val calculatorService: CalculatorService,
) {
    @QueryMapping
    fun getAllCalculations(
        @Argument page: Int,
    ): PaginatedCalculations =
        calculatorService
            .getAllCalculations(page)
            .let { result ->
                PaginatedCalculations(
                    result.content,
                    result.number,
                    result.totalPages,
                )
            }

    @MutationMapping
    fun createCalculation(
        @Argument firstValue: Long,
        @Argument secondValue: Long,
        @Argument operation: CalculatorOperation,
        authentication: Authentication?,
    ): CalcVm {
        val username =
            authentication
                ?.takeUnless { it is AnonymousAuthenticationToken }
                ?.name

        return calculatorService.calculate(
            firstValue,
            secondValue,
            operation,
            username,
        )
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('USER', 'GITHUB_USER') and @calculatorSecurityService.isAllowedToDelete(#ids, authentication)")
    fun deleteCalculations(
        @Argument ids: List<Long>,
    ): Boolean {
        calculatorService.deleteByIds(ids)
        return true
    }
}
