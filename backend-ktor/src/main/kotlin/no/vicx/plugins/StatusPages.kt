package no.vicx.plugins

import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import no.vicx.error.ApiError

const val VALIDATION_ERROR = "validation error"

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respond(
                status = HttpStatusCode.NotFound,
                message = mapOf("error" to cause.message)
            )
        }

        exception<RequestValidationException> { call, cause ->
            call.respondText(
                text = Json.encodeToString(
                    ApiError(
                        status = HttpStatusCode.BadRequest.value,
                        message = VALIDATION_ERROR,
                        url = call.request.uri,
                        validationErrors = cause.reasons
                            .associateBy { errorMessage ->
                                errorMessage.split(" ")
                                    .first()
                                    .replaceFirstChar { it.lowercase() }
                            }
                    )
                ),
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.BadRequest
            )
        }

        exception<Throwable> { call, cause ->
            if (call.request.path().startsWith("/graphql")) {
                defaultGraphQLStatusPages()
            } else {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf(
                        "error" to "An unexpected error occurred",
                        "cause" to (cause.message ?: "No additional details available")
                    )
                )
            }
        }
    }
}