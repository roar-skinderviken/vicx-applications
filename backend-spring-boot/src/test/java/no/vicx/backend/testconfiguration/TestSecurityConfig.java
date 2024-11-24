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

    public static final Jwt JWT_IN_TEST = new Jwt(
            "token",
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