package no.vicx.backend.jwt.github.vm;

import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record GitHubUserResponseVm(
        GitHubUserVm user,
        String grantedScopes,
        String additionalEmailAddress,
        String token) {

    public static final String CLAIM_SUB = "sub";
    public static final String CLAIM_NAME = "name";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_IMAGE = "image";
    public static final String CLAIM_SCOPES = "scopes";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_IAT = "iat";
    public static final String CLAIM_EXP = "exp";

    public static final String HEADER_ALG = "alg";
    public static final String HEADER_ALG_VALUE = "none";

    static final int EXPIRES_AT_IN_SECS = 3600;

    public Jwt toJwt() {
        var defaultClaims = Map.of(
                CLAIM_SUB, user.login(),
                CLAIM_SCOPES, grantedScopes,
                CLAIM_ROLES, Collections.singletonList("ROLE_GITHUB_USER"),
                CLAIM_IAT, Instant.now().getEpochSecond(),
                CLAIM_EXP, Instant.now().plusSeconds(3600).getEpochSecond()
        );

        var claims = new HashMap<>(defaultClaims);

        var emailAddress = user.email() == null || user.email().isBlank()
                ? additionalEmailAddress
                : user.email();

        Optional.ofNullable(emailAddress).ifPresent(it -> claims.put(CLAIM_EMAIL, it));
        Optional.ofNullable(user.name()).ifPresent(it -> claims.put(CLAIM_NAME, it));
        Optional.ofNullable(user.avatarUrl()).ifPresent(it -> claims.put(CLAIM_IMAGE, it));

        var headers = Collections.singletonMap(HEADER_ALG, HEADER_ALG_VALUE);

        return Jwt.withTokenValue(token)
                .headers(h -> h.putAll(headers))
                .claims(c -> c.putAll(claims))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRES_AT_IN_SECS))
                .build();
    }
}
