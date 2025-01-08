package no.vicx.backend.testconfiguration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@TestConfiguration
public class TestSecurityConfig {

    public static final String AUTH_HEADER_IN_TEST = "Bearer some-token";

    public static final String VALID_JWT_STRING = "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9" +
            ".eyJzdWIiOiAiMTIzNDU2Nzg5MCIsICJuYW1lIjogIkpvaG4gRG9lIiwgImlhdCI6IDE1MTYyMzkwMjJ9" +
            ".aGVsbG9fc2lnbmF0dXJlX2Jhc2U2NA==";

    public static OAuth2AuthenticatedPrincipal createPrincipalInTest(List<String> roles) {
        return new OAuth2AuthenticatedPrincipal() {
            @Override
            public Map<String, Object> getAttributes() {
                return Collections.emptyMap();
            }

            @Override
            public List<SimpleGrantedAuthority> getAuthorities() {
                return roles.stream().map(SimpleGrantedAuthority::new).toList();
            }

            @Override
            public String getName() {
                return "user1";
            }
        };

    }

    public static Jwt createJwtInTest(List<String> roles) {
        return Jwt
                .withTokenValue(VALID_JWT_STRING)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(30))
                .header("alg", "none")
                .claim("sub", "user1")
                .claim("roles", roles)
                .build();
    }
}