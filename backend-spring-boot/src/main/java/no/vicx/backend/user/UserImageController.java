package no.vicx.backend.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import no.vicx.backend.error.NotFoundException;
import no.vicx.backend.user.service.UserImageService;
import no.vicx.backend.user.validation.ProfileImage;
import no.vicx.database.user.UserImageRepository;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@Validated
@RestController
@RequestMapping("/api/user/image")
@Tag(name = "UserImage", description = "API for adding, replacing and deleting user image")
@SecurityRequirement(name = "security_auth")
public class UserImageController {

    private final UserImageService userImageService;
    private final UserImageRepository userImageRepository;

    public UserImageController(UserImageService userImageService, UserImageRepository userImageRepository) {
        this.userImageService = userImageService;
        this.userImageRepository = userImageRepository;
    }

    @Operation(
            summary = "Upload or update a user profile image",
            description = "Allows the authenticated user to upload or replace their profile image.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Image uploaded successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid image format or file too large", content = @Content),
                    @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Server error", content = @Content)})
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createUserImage(
            @Parameter(description = "The profile image to upload.", required = true)
            @NotNull @ProfileImage MultipartFile image,

            Authentication authentication) throws IOException {

        userImageService.addOrReplaceUserImage(image, authentication.getName());

        return ResponseEntity
                .created(URI.create("/api/user/image"))
                .build();
    }

    @Operation(
            summary = "Retrieve the user's profile image",
            description = "Fetches the profile image of the currently authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image retrieved successfully.",
                            content = {
                                    @Content(
                                            mediaType = IMAGE_PNG_VALUE,
                                            schema = @Schema(type = "string", format = "binary")),
                                    @Content(
                                            mediaType = IMAGE_JPEG_VALUE,
                                            schema = @Schema(type = "string", format = "binary"))}),
                    @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Image not found for the user", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Server error", content = @Content)})
    @GetMapping(produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getUserImage(Authentication authentication) {
        var userImage = userImageRepository.findByUserUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Image for user " + authentication.getName() + " not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(userImage.getContentType()))
                .cacheControl(CacheControl.noStore())
                .body(userImage.getImageData());
    }

    @Operation(
            summary = "Delete the user's profile image",
            description = "Deletes the profile image of the currently authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Image deleted successfully", content = @Content),
                    @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
                    @ApiResponse(responseCode = "403", description = "User not allowed", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Image not found for the user", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Server error", content = @Content)})
    @DeleteMapping
    public ResponseEntity<Void> deleteUserImage(Authentication authentication) {
        userImageService.deleteUserImage(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}