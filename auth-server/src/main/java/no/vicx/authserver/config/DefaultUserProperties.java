package no.vicx.authserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("default-user")
public record DefaultUserProperties(
        String username,
        String password,
        String name,
        String email) {
}
