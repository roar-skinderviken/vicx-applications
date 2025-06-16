package no.vicx.backend.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.OAuthFlow
import io.swagger.v3.oas.annotations.security.OAuthFlows
import io.swagger.v3.oas.annotations.security.OAuthScope
import io.swagger.v3.oas.annotations.security.SecurityScheme


@OpenAPIDefinition(
    info = Info(
        title = "Vicx API",
        description = "The Vicx API provides endpoints for managing resources, performing operations, " +
                "and accessing features of the Vicx platform.",
        version = "v1"
    )
)
@SecurityScheme(
    name = "security_auth",
    type = SecuritySchemeType.OAUTH2,
    flows = OAuthFlows(
        authorizationCode = OAuthFlow(
            authorizationUrl = "\${springdoc.oAuthFlow.authorizationUrl}",
            tokenUrl = "\${springdoc.oAuthFlow.tokenUrl}",
            scopes = [OAuthScope(name = "openid", description = "openid scope")]
        )
    )
)
class OpenApiConfig
