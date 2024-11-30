package no.vicx.authserver.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties("oauth")
public record OAuthProperties(
        @NotBlank String clientId,
        @NotBlank String clientSecret,
        @NotBlank String redirectUri,
        @NotBlank String postLogoutRedirectUri,
        @NotNull Duration accessTokenTimeToLive,
        @NotNull Duration refreshTokenTimeToLive) {
}
