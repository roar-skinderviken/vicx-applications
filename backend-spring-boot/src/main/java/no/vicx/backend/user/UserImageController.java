package no.vicx.backend.user;

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

@Validated
@RestController
@RequestMapping("/api/user/image")
public class UserImageController {

    private final UserImageService userImageService;
    private final UserImageRepository userImageRepository;

    public UserImageController(UserImageService userImageService, UserImageRepository userImageRepository) {
        this.userImageService = userImageService;
        this.userImageRepository = userImageRepository;
    }

    @PostMapping
    public ResponseEntity<Void> createUserImage(
            @ProfileImage MultipartFile image,
            Authentication authentication) throws IOException {

        userImageService.addOrReplaceUserImage(image, authentication.getName());

        return ResponseEntity
                .created(URI.create("/api/user/image"))
                .build();
    }

    @GetMapping
    public ResponseEntity<byte[]> getUserImage(Authentication authentication) {
        var userImage = userImageRepository.findByUserUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Image for user " + authentication.getName() + " not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(userImage.getContentType()))
                .cacheControl(CacheControl.noStore())
                .body(userImage.getImageData());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserImage(Authentication authentication) {
        userImageService.deleteUserImage(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
