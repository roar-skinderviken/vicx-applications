package no.vicx.backend.user;

import jakarta.validation.Valid;
import no.vicx.backend.user.service.UserService;
import no.vicx.backend.user.validation.ProfileImage;
import no.vicx.backend.user.vm.UserVm;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createUser(
            @Validated UserVm userVm,
            @ProfileImage(
                    invalidFileTypeMessage = "{vicx.constraints.ProfileImage.type.message}",
                    invalidSizeMessage = "{vicx.constraints.ProfileImage.size.message}"
            )
            MultipartFile image) throws IOException {

        var createdUser = userService.createUser(userVm, image);

        return ResponseEntity
                .created(URI.create("/api/user/" + createdUser.getId()))
                .body("User created successfully.");
    }

    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#username == authentication.getName()")
    UserVm getUser(@PathVariable("username") String username) {
        return UserVm.fromVicxUser(userService.getUserByUserName(username));
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userVm.username == authentication.getName()")
    UserVm updateUser(@Valid @RequestBody UserVm userVm) {
        return UserVm.fromVicxUser(userService.updateUser(userVm));
    }
}
