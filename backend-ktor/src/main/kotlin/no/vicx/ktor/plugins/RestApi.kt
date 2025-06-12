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
import no.vicx.ktor.user.vm.ChangePasswordVm
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

private val jsonIgnoreUnknown = Json {
    ignoreUnknownKeys = true
}

fun Application.configureRestApi(
    esportService: EsportService,
    userService: UserService,
    userImageService: UserImageService
) {
    // because of GraphQL, we cannot use global ContentNegotiation, hence respondText

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
                    val userPatchVm = jsonIgnoreUnknown.decodeFromString<UserPatchVm>(call.receiveText())

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
                                        userImageService.addOrReplaceUserImage(
                                            userImageResult.userImage, username
                                        )

                                    is UserImageResult.InvalidImageResult ->
                                        throw RequestValidationException(
                                            IMAGE_PART,
                                            userImageResult.validationError.reasons
                                        )
                                }
                            }
                        )

                        call.respond(HttpStatusCode.Created)
                    }

                    get("/image") {
                        val username = call.getAuthenticatedUsername()

                        val user = userService.getUserByUserName(username)
                        val userImage = user.userImage ?: throw NotFoundException("User image for $username not found")

                        call.response.headers.append(HttpHeaders.CacheControl, "no-store, no-cache, must-revalidate")
                        call.response.headers.append(HttpHeaders.Expires, "0")

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

                    patch("/password") {
                        val username = call.getAuthenticatedUsername()
                        val changePasswordVm = jsonIgnoreUnknown.decodeFromString<ChangePasswordVm>(call.receiveText())

                        changePasswordVm.validate().also { validationResult ->
                            if (validationResult is ValidationResult.Invalid) {
                                throw RequestValidationException(changePasswordVm, validationResult.reasons)
                            }
                        }

                        // will throw validation error when wrong existing password
                        userService.tryUpdatePassword(changePasswordVm, username)

                        call.respondText(
                            text = "Your password has been successfully updated.",
                            contentType = ContentType.Text.Plain,
                            status = HttpStatusCode.OK
                        )
                    }
                }
            }

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