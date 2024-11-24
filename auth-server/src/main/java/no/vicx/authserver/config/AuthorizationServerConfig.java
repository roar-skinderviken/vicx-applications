package no.vicx.authserver.config;

import org.springframework.beans.factory.annotation.Value;
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

import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Value("${oauth.client-id}")
    private String clientId;

    @Value("${oauth.client-secret}")
    private String plainClientSecret;

    @Value("${oauth.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.post-logout-redirect-uri}")
    private String postLogoutRedirectUri;

    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        var registeredClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .clientSecret(passwordEncoder.encode(plainClientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                //.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri(redirectUri)
                .postLogoutRedirectUri(postLogoutRedirectUri)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .scope("message.read")
                .scope("message.write")
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }
}