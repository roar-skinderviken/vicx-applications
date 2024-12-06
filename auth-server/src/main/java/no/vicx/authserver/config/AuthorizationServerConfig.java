package no.vicx.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            OAuthProperties oAuthProperties,
            PasswordEncoder passwordEncoder) {
        var registeredClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(oAuthProperties.clientId())
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .clientSecret(passwordEncoder.encode(oAuthProperties.clientSecret()))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                //.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri(oAuthProperties.redirectUri())
                .postLogoutRedirectUri(oAuthProperties.postLogoutRedirectUri())
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(oAuthProperties.accessTokenTimeToLive())
                        .refreshTokenTimeToLive(oAuthProperties.refreshTokenTimeToLive())
                        .reuseRefreshTokens(false)
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }
}