package no.vicx.authserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import java.util.UUID

@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {
    @Bean
    fun authorizationService(): OAuth2AuthorizationService = InMemoryOAuth2AuthorizationService()

/*
    @Bean
    fun authorizationConsentService(): OAuth2AuthorizationConsentService = InMemoryOAuth2AuthorizationConsentService()
*/

    @Bean
    fun registeredClientRepository(
        oAuthProperties: OAuthProperties,
        passwordEncoder: PasswordEncoder,
    ): RegisteredClientRepository {
        val registeredClient =
            RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(oAuthProperties.clientId)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .clientSecret(passwordEncoder.encode(oAuthProperties.clientSecret))
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(oAuthProperties.redirectUri)
                .redirectUri(oAuthProperties.resourceServer + "/swagger-ui/oauth2-redirect.html")
                .redirectUri(oAuthProperties.resourceServer + "/webjars/swagger-ui/oauth2-redirect.html")
                .redirectUri("http://localhost:8085/login/oauth2/code/messaging-client-oidc")
                .redirectUri("http://localhost:8085/authorized")
                .postLogoutRedirectUri(oAuthProperties.postLogoutRedirectUri)
                .postLogoutRedirectUri("http://localhost:8085/logged-out")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .tokenSettings(
                    TokenSettings
                        .builder()
                        .accessTokenTimeToLive(oAuthProperties.accessTokenTimeToLive)
                        .refreshTokenTimeToLive(oAuthProperties.refreshTokenTimeToLive)
                        .build(),
                ).build()

        return InMemoryRegisteredClientRepository(registeredClient)
    }
}
