package no.vicx.backend.user;

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
public class PasswordController {
    static final String PASSWORD_CHANGED_BODY_TEXT = "Your password has been successfully updated.";

    private final UserService userService;

    public PasswordController(UserService userService) {
        this.userService = userService;
    }


    @PatchMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> changePassword(
            @Validated @RequestBody ChangePasswordVm changePasswordVm,
            Authentication authentication) {

        userService.updatePassword(changePasswordVm, authentication.getName());

        return ResponseEntity
                .ok()
                .body(PASSWORD_CHANGED_BODY_TEXT);
    }
}
