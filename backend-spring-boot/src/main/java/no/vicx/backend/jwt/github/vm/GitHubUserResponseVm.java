package no.vicx.backend.jwt.github.vm;

import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static no.vicx.backend.jwt.JwtConstants.*;

public record GitHubUserResponseVm(
        GitHubUserVm user,
        String grantedScopes,
        String additionalEmailAddress,
        String token) {

    static final int EXPIRES_AT_IN_SECS = 3600;

    public Jwt toJwt() {
        var defaultClaims = Map.of(
                CLAIM_SCOPES, grantedScopes,
                CLAIM_ROLES, Collections.singletonList("GITHUB_USER")
        );

        var emailAddress = user.email() == null || user.email().isBlank()
                ? additionalEmailAddress
                : user.email();

        var claims = new HashMap<>(defaultClaims);

        Optional.ofNullable(emailAddress).ifPresent(it -> claims.put(CLAIM_EMAIL, it));
        Optional.ofNullable(user.name()).ifPresent(it -> claims.put(CLAIM_NAME, it));
        Optional.ofNullable(user.avatarUrl()).ifPresent(it -> claims.put(CLAIM_IMAGE, it));

        return Jwt.withTokenValue(token)
                .subject(user.login())
                .headers(h -> h.put(HEADER_ALG, HEADER_ALG_NONE))
                .claims(c -> c.putAll(claims))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRES_AT_IN_SECS))
                .build();
    }
}
