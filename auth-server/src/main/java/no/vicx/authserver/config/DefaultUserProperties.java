package no.vicx.authserver.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("default-user")
public record DefaultUserProperties(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank String email) {
}
