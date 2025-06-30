package no.vicx.ktor.plugins

import com.expediagroup.graphql.generator.extensions.plus
import com.expediagroup.graphql.server.ktor.DefaultKtorGraphQLContextFactory
import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphQLSDLRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import graphql.GraphQLContext
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.AuthenticationStrategy
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.routing.routing
import no.vicx.ktor.calculator.CalculatorService
import no.vicx.ktor.calculator.query.CalculatorQuery
import no.vicx.ktor.db.repository.CalculatorRepository

suspend fun Application.configureGraphQL() {
    val calculatorService: CalculatorService = dependencies.resolve()
    val calculatorRepository: CalculatorRepository = dependencies.resolve()

    install(GraphQL) {
        schema {
            packages =
                listOf(
                    "no.vicx.ktor.calculator.vm",
                    "no.vicx.ktor.calculator.query",
                    "no.vicx.ktor.db.model",
                )

            queries =
                listOf(
                    CalculatorQuery(calculatorService),
                )

            mutations =
                listOf(
                    no.vicx.ktor.calculator.query
                        .CalculatorMutation(calculatorService, calculatorRepository),
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

        return request.call
            .principal<JWTPrincipal>()
            ?.let { jwtPrincipal -> baseContext.plus(mapOf(JWT_PRINCIPAL_KEY to jwtPrincipal)) }
            ?: baseContext
    }

    companion object {
        const val JWT_PRINCIPAL_KEY = "jwtPrincipal"
    }
}
