package no.vicx.authserver.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("default-user")
data class DefaultUserProperties(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String,
    @field:NotBlank val name: String,
    @field:NotBlank val email:String
)
