package no.vicx.ktor.plugins

import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kotlinx.serialization.json.Json
import no.vicx.ktor.error.ApiError

const val VALIDATION_ERROR = "validation error"

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respondText(
                text =
                    Json.encodeToString(
                        ApiError(
                            status = HttpStatusCode.NotFound.value,
                            message = cause.message.orEmpty(),
                            url = call.request.uri,
                        ),
                    ),
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.NotFound,
            )
        }

        exception<RequestValidationException> { call, cause ->
            call.respondText(
                text =
                    Json.encodeToString(
                        ApiError(
                            status = HttpStatusCode.BadRequest.value,
                            message = VALIDATION_ERROR,
                            url = call.request.uri,
                            validationErrors =
                                cause.reasons
                                    .associateBy { errorMessage ->
                                        errorMessage
                                            .split(" ")
                                            .first()
                                            .replaceFirstChar { it.lowercase() }
                                    },
                        ),
                    ),
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.BadRequest,
            )
        }

        exception<Throwable> { call, cause ->
            if (call.request.path().startsWith("/graphql")) {
                defaultGraphQLStatusPages()
            } else {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message =
                        mapOf(
                            "error" to "An unexpected error occurred",
                            "cause" to (cause.message ?: "No additional details available"),
                        ),
                )
            }
        }
    }
}
