package no.vicx.ktor.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import no.vicx.ktor.db.model.UserImage
import no.vicx.ktor.esport.EsportService
import no.vicx.ktor.user.service.UserImageService
import no.vicx.ktor.user.service.UserService
import no.vicx.ktor.user.toViewModel
import no.vicx.ktor.user.vm.UserPatchVm
import no.vicx.ktor.util.IMAGE_PART
import no.vicx.ktor.util.UserImageResult
import no.vicx.ktor.util.extractFormItemsAndFileItem

fun List<ValidationResult>.throwIfAnyInvalid(value: Any) {
    val reasons = this
        .filterIsInstance<ValidationResult.Invalid>()
        .flatMap { it.reasons }

    if (reasons.isNotEmpty()) throw RequestValidationException(value, reasons)
}

fun ApplicationCall.getAuthenticatedUsername(): String =
    this.principal<JWTPrincipal>()?.subject
        ?: throw SecurityException("JWTPrincipal or subject is missing for secured endpoint")

fun Application.configureRestApi(
    esportService: EsportService,
    userService: UserService,
    userImageService: UserImageService
) {
    routing {
        route("/api") {

            post("/user") {
                val multiPartData = call.receiveMultipart()
                val validationErrors = mutableListOf<ValidationResult>()
                var userImage: UserImage? = null

                multiPartData.extractFormItemsAndFileItem(
                    userImageCallback = { userImageResult ->
                        when (userImageResult) {
                            is UserImageResult.ValidImageResult ->
                                userImage = userImageResult.userImage

                            is UserImageResult.InvalidImageResult ->
                                validationErrors.add(userImageResult.validationError)
                        }
                    },
                    userCallback = { validationResult, createUserVm ->
                        validationErrors.add(validationResult)
                        validationErrors.throwIfAnyInvalid(createUserVm)
                        userService.createUser(createUserVm, userImage)
                    }
                )

                call.respondText(
                    "User created successfully.",
                    ContentType.Text.Plain,
                    HttpStatusCode.OK
                )
            }

            authenticate(strategy = AuthenticationStrategy.Required) {

                get("/user") {
                    val username = call.getAuthenticatedUsername()

                    call.respondText(
                        Json.encodeToString(userService.getUserByUserName(username).toViewModel()),
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
                        text = "User updated successfully.",
                        contentType = ContentType.Text.Plain,
                        status = HttpStatusCode.OK
                    )
                }

                route("/user") {

                    post("/image") {
                        val username = call.getAuthenticatedUsername()
                        val multiPartData = call.receiveMultipart()

                        multiPartData.extractFormItemsAndFileItem(
                            userImageCallback = { userImageResult ->
                                when (userImageResult) {
                                    is UserImageResult.ValidImageResult ->
                                        userImageService.addOrReplaceUserImage(userImageResult.userImage, username)

                                    is UserImageResult.InvalidImageResult ->
                                        listOf(userImageResult.validationError).throwIfAnyInvalid(IMAGE_PART)
                                }
                            }
                        )

                        call.response.header(HttpHeaders.Location, "/api/user/image")
                        call.respond(HttpStatusCode.Created)
                    }

                    get("/image") {
                        val username = call.getAuthenticatedUsername()

                        val user = userService.getUserByUserName(username)
                        val userImage = user.userImage ?: throw NotFoundException("User image for $username not found")

                        call.response.header(HttpHeaders.CacheControl, "no-store")
                        call.respondBytes(
                            contentType = ContentType.parse(userImage.contentType),
                            status = HttpStatusCode.OK,
                            bytes = userImage.imageData
                        )
                    }

                    delete("/image") {
                        val username = call.getAuthenticatedUsername()
                        userImageService.deleteUserImage(username)
                        call.respond(HttpStatusCode.NoContent)
                    }
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