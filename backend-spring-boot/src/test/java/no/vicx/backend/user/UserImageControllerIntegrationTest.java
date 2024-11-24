package no.vicx.backend.user;

import no.vicx.backend.testconfiguration.TestSecurityConfig;
import no.vicx.database.user.UserImage;
import no.vicx.database.user.UserImageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
class UserImageControllerIntegrationTest {

    @Autowired
    WebTestClient webClient;

    @MockitoBean
    UserImageRepository userImageRepository;

    @Test
    void getUserImage_withoutCredentials_expectUnauthorized() {
        performGetRequest(false, "user1")
                .expectStatus().isUnauthorized();
    }

    @Test
    void getUserImage_withCredentialsForOtherUser_expectForbidden() {
        performGetRequest(true, "user2")
                .expectStatus().isForbidden();
    }

    @Test
    void getUserImage_withCredentials_expectOk() {
        performGetRequest(true, "user1")
                .expectStatus().isOk();

        verify(userImageRepository, times(1)).findByUserUsername("user1");
    }

    @Test
    void getUserImage_imageExistsInDatabase_expectImageInDatabase() {
        var userImage = new UserImage();
        userImage.setImageData(new byte[]{1, 2, 3});
        userImage.setContentType("image/jpeg");

        when(userImageRepository.findByUserUsername(anyString())).thenReturn(Optional.of(userImage));

        performGetRequest(true, "user1")
                .expectStatus().isOk()
                .expectHeader().contentType("image/jpeg");

        verify(userImageRepository, times(1)).findByUserUsername("user1");
    }

    @Test
    void getUserImage_imageDoesNotExistInDatabase_expectDefaultImage() {
        performGetRequest(true, "user1")
                .expectStatus().isOk()
                .expectHeader().contentType("image/png");

        verify(userImageRepository, times(1)).findByUserUsername("user1");
    }

    WebTestClient.ResponseSpec performGetRequest(boolean addCredentials, String username) {
        var requestHeadersSpec = webClient.get()
                .uri("/api/user/image/" + username);

        if (addCredentials) {
            requestHeadersSpec.headers(httpHeaders -> httpHeaders.setBearerAuth("some-token"));
        }

        return requestHeadersSpec.exchange();
    }
}