package no.vicx.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.vicx.calculator.CalculatorService

fun Application.configureSerialization(
    calculatorService: CalculatorService
) {
    routing {
        authenticate {
            route("/calculations") {

                // /calculations/get-all/1

                get("/get-all/{pageNumber}") {
                    val principal: JWTPrincipal? = call.principal<JWTPrincipal>()
                    if (principal != null) {
                        val roles = principal.payload.getClaim("roles")?.asList(String::class.java)
                        if (roles == null || "USER" !in roles) {
                            return@get call.respond(HttpStatusCode.Forbidden, "Insufficient permissions")
                        }
                    }

                    val pageNumber: Int = call.parameters["pageNumber"]?.toIntOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest)

                    val paginatedCalculations = calculatorService.getPagedCalculations(pageNumber)
                    call.respond(paginatedCalculations)
                }
            }
        }
    }
}
