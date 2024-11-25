package no.vicx.backend.testconfiguration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@TestConfiguration
public class TestSecurityConfig {

    public static final String VALID_JWT_STRING = "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9" +
            ".eyJzdWIiOiAiMTIzNDU2Nzg5MCIsICJuYW1lIjogIkpvaG4gRG9lIiwgImlhdCI6IDE1MTYyMzkwMjJ9" +
            ".aGVsbG9fc2lnbmF0dXJlX2Jhc2U2NA==";

    public static final Jwt JWT_IN_TEST = new Jwt(
            VALID_JWT_STRING,
            Instant.now(),
            Instant.now().plusSeconds(30),
            Map.of("alg", "none"),
            Map.of(
                    "sub", "user1",
                    "roles", Collections.singletonList("ROLE_USER"))
    );

    @Primary
    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> JWT_IN_TEST;
    }
}