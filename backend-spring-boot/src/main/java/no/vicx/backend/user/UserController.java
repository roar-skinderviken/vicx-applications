package no.vicx.backend.user;

import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.validation.ProfileImage;
import no.vicx.backend.user.vm.UserPatchRequestVm;
import no.vicx.backend.user.vm.UserVm;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {
    static final String USER_CREATED_BODY_TEXT = "User created successfully.";
    static final String USER_UPDATE_BODY_TEXT = "User updated successfully.";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user if the username does not already exist in the database.
     * <p>
     * <strong>Note:</strong> When using {@link jakarta.validation.Valid}, the class-level validator
     * {@link no.vicx.backend.user.validation.RecaptchaThenUniqueUsername}
     * will run twice and fail reCAPTCHA validation on the second invocation.
     * To avoid this, use {@link org.springframework.validation.annotation.Validated}.
     * </p>
     *
     * @param userVm the user data submitted for creation, validated against {@link no.vicx.backend.user.vm.UserVm}.
     * @param image  an optional multipart file containing the user's profile image.
     *               Validated using the {@link no.vicx.backend.user.validation.ProfileImage} annotation.
     * @return a response containing the location of the created user in the headers,
     * a description of the outcome in the body, and HTTP status code 201 (Created).
     * @throws IOException if an error occurs while processing the profile image.
     */
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createUser(
            @Validated UserVm userVm,
            @ProfileImage MultipartFile image) throws IOException {

        var createdUser = userService.createUser(userVm, image);

        return ResponseEntity
                .created(URI.create("/api/user/" + createdUser.getUsername()))
                .body(USER_CREATED_BODY_TEXT);
    }

    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#username == authentication.getName()")
    UserVm getUser(@PathVariable("username") String username) {
        return UserVm.fromVicxUser(userService.getUserByUserName(username));
    }

    @PatchMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> patchName(
            @Validated @RequestBody UserPatchRequestVm body,
            Authentication authentication) {
        userService.updateUser(body, authentication.getName());

        return ResponseEntity
                .ok()
                .body(USER_UPDATE_BODY_TEXT);
    }
}
