package no.vicx.authserver.config

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import java.time.Duration

@Validated
@ConfigurationProperties("oauth")
class OAuthProperties {
    @NotBlank
    lateinit var clientId: String

    @NotBlank
    lateinit var clientSecret: String

    @NotBlank
    lateinit var redirectUri: String

    @NotBlank
    lateinit var postLogoutRedirectUri: String

    @NotBlank
    lateinit var resourceServer: String

    @NotNull
    var accessTokenTimeToLive: Duration? = null

    @NotNull
    var refreshTokenTimeToLive: Duration? = null
}
