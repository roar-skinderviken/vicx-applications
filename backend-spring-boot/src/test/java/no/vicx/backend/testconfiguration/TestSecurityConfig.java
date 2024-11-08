package no.vicx.backend.testconfiguration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.Map;

@TestConfiguration
public class TestSecurityConfig {

    @Primary
    @Bean
    public JwtDecoder jwtDecoder() {
        return token ->  new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(30),
                Map.of("alg", "none"),
                Map.of("sub", "user1")
        );
    }
}