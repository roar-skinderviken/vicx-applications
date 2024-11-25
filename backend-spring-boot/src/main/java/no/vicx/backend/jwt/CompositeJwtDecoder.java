package no.vicx.backend.jwt;

import no.vicx.backend.jwt.github.GitHubJwtFromOpaqueProducer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.util.Arrays;
import java.util.Base64;

public record CompositeJwtDecoder(
        JwtDecoder nimbusJwtDecoder,
        GitHubJwtFromOpaqueProducer gitHubJwtFromOpaqueProducer) implements JwtDecoder {

    @Override
    public Jwt decode(String token) throws JwtException {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtException("Empty token");
        }

        return isJwt(token)
                ? nimbusJwtDecoder.decode(token)
                : gitHubJwtFromOpaqueProducer.createJwt(token);
    }

    static boolean isJwt(String token) {
        var parts = token.split("\\.");
        if (parts.length != 3) {
            return false;  // A valid JWT should have exactly 3 parts (header, payload, signature)
        }

        return Arrays.stream(parts)
                .allMatch(CompositeJwtDecoder::isValidBase64);
    }

    static boolean isValidBase64(String part) {
        try {
            return Base64.getDecoder().decode(part) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}