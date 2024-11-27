package no.vicx.authserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("oauth")
public record OAuthProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String postLogoutRedirectUri,
        Duration accessTokenTimeToLive,
        Duration refreshTokenTimeToLive) {
}
