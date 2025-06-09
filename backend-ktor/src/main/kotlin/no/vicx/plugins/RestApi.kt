package no.vicx.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import no.vicx.esport.EsportService
import no.vicx.extension.toCreateUserVmAndUserImage
import no.vicx.user.service.UserService
import no.vicx.user.vm.UserPatchVm

fun ApplicationCall.getAuthenticatedUsername(): String =
    this.principal<JWTPrincipal>()?.subject
        ?: throw SecurityException("JWTPrincipal or subject is missing for secured endpoint")

fun Application.configureRestApi(
    esportService: EsportService,
    userService: UserService,
) {
    routing {
        route("/api") {

            post("/user") {
                val multiPartData = call.receiveMultipart()
                val (createUserVm, userImage) = multiPartData.toCreateUserVmAndUserImage()

                createUserVm.validate().also { validationResult ->
                    if (validationResult is ValidationResult.Invalid) {
                        throw RequestValidationException(createUserVm, validationResult.reasons)
                    }
                }

                userService.createUser(createUserVm, userImage)
                call.respond("User created successfully.")
            }

            authenticate(strategy = AuthenticationStrategy.Required) {

                get("/user") {
                    val username = call.getAuthenticatedUsername()

                    call.respondText(
                        Json.encodeToString(userService.getUserByUserName(username)),
                        ContentType.Application.Json,
                        HttpStatusCode.OK
                    )
                }

                patch("/user") {
                    val username = call.getAuthenticatedUsername()
                    val userPatchVm = Json.decodeFromString<UserPatchVm>(call.receiveText())

                    userPatchVm.validate().also { validationResult ->
                        if (validationResult is ValidationResult.Invalid) {
                            throw RequestValidationException(userPatchVm, validationResult.reasons)
                        }
                    }

                    userService.updateUser(
                        userPatchVm,
                        username
                    )

                    call.respondText(
                        "User updated successfully.",
                        ContentType.Text.Plain,
                        HttpStatusCode.OK
                    )
                }
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