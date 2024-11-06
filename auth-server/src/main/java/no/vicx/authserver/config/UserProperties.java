package no.vicx.authserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app-user")
public record UserProperties(String name, String password) {
}