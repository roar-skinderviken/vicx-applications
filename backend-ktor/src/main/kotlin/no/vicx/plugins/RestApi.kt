package no.vicx.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import no.vicx.esport.EsportService
import no.vicx.extensions.toCreateUserVm
import no.vicx.extensions.toUserImage
import no.vicx.user.service.UserService

fun Application.configureRestApi(
    esportService: EsportService,
    userService: UserService,
) {
    routing {
        route("/api") {

            post("/user") {
                val multipart: MultiPartData = call.receiveMultipart()
                val createUserVm = multipart.toCreateUserVm()

                createUserVm.validate().also { validationResult ->
                    if (validationResult is ValidationResult.Invalid) {
                        throw RequestValidationException(createUserVm, validationResult.reasons)
                    }
                }

                userService.createUser(
                    createUserVm,
                    multipart.toUserImage()
                )

                call.respond("User created successfully.")
            }

            patch("/user") {
                call.respond("User updated successfully.")
            }
            
            // because of GraphQL, we cannot use ContentNegotiation, hence respondText
            get("/esport") {
                call.respondText(
                    Json.encodeToString(esportService.getMatches()),
                    ContentType.Application.Json,
                    HttpStatusCode.OK
                )
            }
        }
    }
}