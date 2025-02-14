package no.vicx.authserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenStatusController {

    private static final Logger LOG = LoggerFactory.getLogger(TokenStatusController.class);

    private final OAuth2AuthorizationService authorizationService;

    public TokenStatusController(OAuth2AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("token-status")
    public ResponseEntity<?> tokenStatus(@RequestParam String token) {

        var authorization =
                authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);

        if (authorization != null) {
            var accessToken = authorization.getAccessToken();

            if (accessToken.isInvalidated()) {
                LOG.info("Token isInvalidated: {}", token);
                return ResponseEntity.ok("Token isInvalidated");
            } else {
                LOG.info("Token not invalidated: {}", token);
                return ResponseEntity.ok("Token not invalidated");
            }
        }

        LOG.warn("Non-existent token: {}", token);
        return ResponseEntity.notFound().build();
    }
}
