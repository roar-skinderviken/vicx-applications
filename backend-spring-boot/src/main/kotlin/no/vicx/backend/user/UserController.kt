package no.vicx.backend.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import no.vicx.backend.user.service.UserService
import no.vicx.backend.user.validation.ProfileImage
import no.vicx.backend.user.vm.CreateUserVm
import no.vicx.backend.user.vm.UserPatchVm
import no.vicx.backend.user.vm.UserVm
import no.vicx.backend.user.vm.UserVm.Companion.fromVicxUser
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URI


@RestController
@RequestMapping("/api/user")
@Validated
@Tag(name = "User", description = "API for adding and updating users")
class UserController(
    private val userService: UserService
) {
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user if the username does not already exist in the database. " +
                "An optional profile image can be provided.",
        responses = [ApiResponse(
            responseCode = "201",
            description = "User created successfully.",
            content = arrayOf(
                Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = Schema(type = "string", example = USER_CREATED_BODY_TEXT)
                )
            )
        ), ApiResponse(responseCode = "400", description = "Invalid input data", content = [Content()]), ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = [Content()]
        )]
    )
    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE]
    )
    fun createUser(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User data",
            required = true
        )
        @Validated createUserVm: CreateUserVm,
        @Parameter(description = "Profile image")
        @ProfileImage image: MultipartFile?
    ): ResponseEntity<String> {
        userService.createUser(createUserVm, image)

        return ResponseEntity
            .created(URI.create("/api/user"))
            .body(USER_CREATED_BODY_TEXT)
    }

    @SecurityRequirement(name = "security_auth")
    @Operation(
        summary = "Get the currently authenticated user",
        description = "Fetches the details of the currently authenticated user.",
        security = [SecurityRequirement(name = "bearerAuth")],
        responses = [ApiResponse(
            responseCode = "200",
            description = "User found",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = UserVm::class)
            )]
        ), ApiResponse(
            responseCode = "401",
            description = "User not authenticated",
            content = [Content()]
        ), ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = [Content()]
        ), ApiResponse(responseCode = "404", description = "User not found", content = [Content()])]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUser(authentication: Authentication): UserVm {
        return fromVicxUser(userService.getUserByUserName(authentication.name))
    }

    @SecurityRequirement(name = "security_auth")
    @Operation(
        summary = "Update user name or email",
        description = "Allows the user to update their name or email using a patch operation.",
        responses = [ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = [Content(
                mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = Schema(type = "string", example = USER_UPDATE_BODY_TEXT)
            )]
        ), ApiResponse(responseCode = "400", description = "Invalid input data", content = [Content()]), ApiResponse(
            responseCode = "401",
            description = "User not authenticated",
            content = [Content()]
        ), ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = [Content()]
        ), ApiResponse(responseCode = "404", description = "User not found", content = [Content()])]
    )
    @PatchMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun patchName(
        @Parameter(description = "The user data to update", required = true)
        @Validated
        @RequestBody
        body: UserPatchVm,
        authentication: Authentication
    ): ResponseEntity<String> {
        userService.updateUser(body, authentication.name)
        return ResponseEntity.ok(USER_UPDATE_BODY_TEXT)
    }

    companion object {
        const val USER_CREATED_BODY_TEXT = "User created successfully."
        const val USER_UPDATE_BODY_TEXT = "User updated successfully."
    }
}
