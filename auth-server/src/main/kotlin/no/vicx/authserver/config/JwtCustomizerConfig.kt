package no.vicx.authserver.config

import no.vicx.authserver.CustomUserDetails
import no.vicx.authserver.loggerFor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer

@Configuration(proxyBeanMethods = false)
class JwtCustomizerConfig {

    @Bean
    fun jwtCustomizer() = OAuth2TokenCustomizer { jwtEncodingContext: JwtEncodingContext ->
        log.info("Received token type: {} (value={})",
            jwtEncodingContext.tokenType,
            jwtEncodingContext.tokenType.value
        )

        val authentication = jwtEncodingContext.getPrincipal<Authentication?>() ?: return@OAuth2TokenCustomizer
        val builder: JwtClaimsSet.Builder = jwtEncodingContext.claims

        builder.claim(
            ROLES_CLAIM,
            authentication.authorities
                .map { grantedAuthority -> grantedAuthority.authority }
        )

        if (jwtEncodingContext.tokenType.value != OidcParameterNames.ID_TOKEN)
            return@OAuth2TokenCustomizer

        val userDetails = authentication.principal as? CustomUserDetails ?: return@OAuth2TokenCustomizer

        builder.addIdTokenClaims(
            userDetails = userDetails,
            authorizedScopes = jwtEncodingContext.authorizedScopes
        )
    }

    companion object {
        const val ROLES_CLAIM = "roles"
        const val NAME_CLAIM = "name"
        const val IMAGE_CLAIM = "image"
        const val EMAIL_CLAIM = "email"

        private val log = loggerFor<JwtCustomizerConfig>()

        private fun JwtClaimsSet.Builder.addIdTokenClaims(
            userDetails: CustomUserDetails,
            authorizedScopes: Set<String>
        ) {
            if (OidcScopes.EMAIL in authorizedScopes)
                claim(EMAIL_CLAIM, userDetails.email)

            if (OidcScopes.PROFILE in authorizedScopes) {
                claim(NAME_CLAIM, userDetails.name)

                if (userDetails.hasImage)
                    claim(IMAGE_CLAIM, userDetails.username)
            }
        }
    }
}