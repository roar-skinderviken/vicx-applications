package no.vicx.calculator.query

import com.expediagroup.graphql.server.operations.Mutation
import graphql.schema.DataFetchingEnvironment
import io.ktor.server.auth.jwt.*
import no.vicx.calculator.CalculatorService
import no.vicx.calculator.vm.CalcVm
import no.vicx.db.model.CalculatorOperation
import no.vicx.db.repository.CalculatorRepository
import no.vicx.plugins.CustomGraphQLContextFactory.Companion.JWT_PRINCIPAL_KEY

@Suppress("unused")
class CalculatorMutation(
    private val calculatorService: CalculatorService,
    private val calculatorRepository: CalculatorRepository
) : Mutation {

    suspend fun deleteCalculations(
        ids: List<Int>,
        environment: DataFetchingEnvironment
    ): Boolean {
        val jwtPrincipal = environment.graphQlContext.get<JWTPrincipal>(JWT_PRINCIPAL_KEY)
            ?: throw RuntimeException("Unauthorized")

        if (!calculatorService.isAllowedToDelete(ids, jwtPrincipal.subject!!)) {
            throw RuntimeException("Forbidden")
        }

        return calculatorRepository.deleteByIdIn(ids.map { it.toLong() }) > 0
    }

    suspend fun createCalculation(
        firstValue: Int,
        secondValue: Int,
        operation: CalculatorOperation,
        environment: DataFetchingEnvironment
    ): CalcVm {
        val jwtPrincipal = environment.graphQlContext.get<JWTPrincipal>(JWT_PRINCIPAL_KEY)
        val username = jwtPrincipal?.subject ?: ANONYMOUS_USERNAME

        return calculatorService.calculate(
            firstValue.toLong(),
            secondValue.toLong(),
            operation,
            username
        )
    }

    companion object {
        const val ANONYMOUS_USERNAME = "Anonymous"
    }
}