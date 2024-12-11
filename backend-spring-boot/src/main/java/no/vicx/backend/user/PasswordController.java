package no.vicx.backend.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.vm.ChangePasswordVm;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/password")
@Validated
@Tag(name = "Password", description = "API for updating user password")
@SecurityRequirement(name = "security_auth")
public class PasswordController {
    static final String PASSWORD_CHANGED_BODY_TEXT = "Your password has been successfully updated.";

    private final UserService userService;

    public PasswordController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Change user password",
            description = "Allows the authenticated user to update their password.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password updated successfully.",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string", example = PASSWORD_CHANGED_BODY_TEXT))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                    @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Server error", content = @Content)})
    @PatchMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> changePassword(
            @Parameter(
                    description = "The new password and confirmation provided by the user.",
                    required = true)
            @Validated @RequestBody ChangePasswordVm changePasswordVm,
            Authentication authentication) {

        userService.updatePassword(changePasswordVm, authentication.getName());

        return ResponseEntity.ok(PASSWORD_CHANGED_BODY_TEXT);
    }
}
