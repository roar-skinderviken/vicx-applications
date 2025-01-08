package no.vicx.backend.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public final class JwtUtils {
    private JwtUtils() {
    }

    public static final String BEARER_PREFIX = "Bearer ";

    public static boolean useJwt(final HttpServletRequest request) {
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return false;
        }
        var token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        var parts = token.split("\\.");
        return parts.length == 3;
    }
}
