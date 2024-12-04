package no.vicx.backend.user;

import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.validation.ProfileImage;
import no.vicx.backend.user.vm.UserPatchVm;
import no.vicx.backend.user.vm.CreateUserVm;
import no.vicx.backend.user.vm.UserVm;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
     * @param createUserVm the user data submitted for creation, validated against {@link CreateUserVm}.
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
            @Validated CreateUserVm createUserVm,
            @ProfileImage MultipartFile image) throws IOException {

        userService.createUser(createUserVm, image);

        return ResponseEntity
                .created(URI.create("/api/user"))
                .body(USER_CREATED_BODY_TEXT);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    UserVm getUser(Authentication authentication) {
        return UserVm.fromVicxUser(userService.getUserByUserName(authentication.getName()));
    }

    @PatchMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> patchName(
            @Validated @RequestBody UserPatchVm body,
            Authentication authentication) {
        userService.updateUser(body, authentication.getName());

        return ResponseEntity
                .ok()
                .body(USER_UPDATE_BODY_TEXT);
    }
}
