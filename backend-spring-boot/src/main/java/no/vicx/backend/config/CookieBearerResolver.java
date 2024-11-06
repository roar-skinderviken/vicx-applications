package no.vicx.backend.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

import java.util.Arrays;
import java.util.Optional;

record CookieBearerResolver(String cookieName) implements BearerTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cs -> Arrays.stream(cs)
                        .filter(cookie -> cookie.getName().equals(cookieName))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElse(null);
    }
}
