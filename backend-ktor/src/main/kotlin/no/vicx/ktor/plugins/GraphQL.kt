package no.vicx.ktor.plugins

import com.expediagroup.graphql.generator.extensions.plus
import com.expediagroup.graphql.server.ktor.*
import graphql.GraphQLContext
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import no.vicx.ktor.calculator.CalculatorService
import no.vicx.ktor.calculator.query.CalculatorQuery
import no.vicx.ktor.db.repository.CalculatorRepository

fun Application.configureGraphQL(
    calculatorService: CalculatorService,
    calculatorRepository: CalculatorRepository
) {
    install(GraphQL) {
        schema {
            packages = listOf(
                "no.vicx.ktor.calculator.vm",
                "no.vicx.ktor.calculator.query",
                "no.vicx.ktor.db.model"
            )

            queries = listOf(
                CalculatorQuery(calculatorService)
            )

            mutations = listOf(
                no.vicx.ktor.calculator.query.CalculatorMutation(calculatorService, calculatorRepository)
            )

            server {
                contextFactory = CustomGraphQLContextFactory()
            }
        }
    }

    routing {
        authenticate(strategy = AuthenticationStrategy.Optional) {
            graphQLPostRoute()
        }
        graphiQLRoute()
        graphQLSDLRoute()
    }
}

/**
 * Custom logic for adding [JWTPrincipal] to the [GraphQLContext]
 */
class CustomGraphQLContextFactory : DefaultKtorGraphQLContextFactory() {
    override suspend fun generateContext(request: ApplicationRequest): GraphQLContext {
        val baseContext = super.generateContext(request)

        return request.call.principal<JWTPrincipal>()
            ?.let { jwtPrincipal -> baseContext.plus(mapOf(JWT_PRINCIPAL_KEY to jwtPrincipal)) }
            ?: baseContext
    }

    companion object {
        const val JWT_PRINCIPAL_KEY = "jwtPrincipal"
    }
}
