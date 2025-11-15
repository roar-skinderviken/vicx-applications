package no.vicx.authserver.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("default-user")
class DefaultUserProperties {
    @NotBlank
    lateinit var username: String

    @NotBlank
    lateinit var password: String

    @NotBlank
    lateinit var name: String

    @NotBlank
    lateinit var email: String
}
