package no.vicx.backend.user;

import no.vicx.database.user.UserImageRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/user/image")
public class UserImageController {

    private static final Pair<String, byte[]> DEFAULT_PROFILE_IMAGE;

    static {
        try {
            DEFAULT_PROFILE_IMAGE = Pair.of(
                    MediaType.IMAGE_PNG_VALUE,
                    new ClassPathResource("profile.png").getContentAsByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final UserImageRepository userImageRepository;

    public UserImageController(UserImageRepository userImageRepository) {
        this.userImageRepository = userImageRepository;
    }

    @GetMapping("/{username}")
    @PreAuthorize("#username == authentication.getName()")
    public ResponseEntity<byte[]> getUserImage(@PathVariable String username) {

        var userImage = userImageRepository.findByUserUsername(username)
                .map(it -> Pair.of(it.getContentType(), it.getImageData()))
                .orElse(DEFAULT_PROFILE_IMAGE);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(userImage.getFirst()));
        headers.setCacheControl("public, max-age=3600");

        return new ResponseEntity<>(userImage.getSecond(), headers, HttpStatus.OK);
    }
}
