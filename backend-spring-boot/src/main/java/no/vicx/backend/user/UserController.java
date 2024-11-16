package no.vicx.backend.user;

import jakarta.validation.Valid;
import no.vicx.backend.user.vm.UserVm;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> createUser(@Valid @RequestBody UserVm userVm) {
        var createdUser = userService.createUser(userVm.toNewVicxUser());

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
