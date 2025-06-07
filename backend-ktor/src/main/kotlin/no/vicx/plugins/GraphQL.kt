package no.vicx.plugins

import com.expediagroup.graphql.generator.extensions.plus
import com.expediagroup.graphql.server.ktor.*
import graphql.GraphQLContext
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import no.vicx.calculator.CalculatorService
import no.vicx.calculator.query.CalculatorMutation
import no.vicx.calculator.query.CalculatorQuery
import no.vicx.db.repository.CalculatorRepository

fun Application.configureGraphQL(
    calculatorService: CalculatorService,
    calculatorRepository: CalculatorRepository
) {
    install(GraphQL) {
        schema {
            packages = listOf(
                "no.vicx.calculator.vm",
                "no.vicx.calculator.query",
                "no.vicx.db.dto"
            )

            queries = listOf(
                CalculatorQuery(calculatorService)
            )

            mutations = listOf(
                CalculatorMutation(calculatorService, calculatorRepository)
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
 * Custom logic for how this example app should create its context given the [ApplicationRequest]
 */
class CustomGraphQLContextFactory : DefaultKtorGraphQLContextFactory() {
    override suspend fun generateContext(request: ApplicationRequest): GraphQLContext {
        val context = super.generateContext(request)
        val principal = request.call.principal<JWTPrincipal>()

        return if (principal != null) context.plus(mapOf("jwtPrincipal" to principal))
        else context
    }
}
