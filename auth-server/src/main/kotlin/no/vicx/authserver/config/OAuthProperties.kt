package no.vicx.authserver.config

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import java.time.Duration

@Validated
@ConfigurationProperties("oauth")
data class OAuthProperties(
    @field:NotBlank val clientId: String,
    @field:NotBlank val clientSecret: String,
    @field:NotBlank val redirectUri: String,
    @field:NotBlank val postLogoutRedirectUri: String,
    @field:NotBlank val resourceServer: String,
    @field:NotNull val accessTokenTimeToLive: Duration?,
    @field:NotNull val refreshTokenTimeToLive: Duration?
)
