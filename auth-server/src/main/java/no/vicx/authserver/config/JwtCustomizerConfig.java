package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.stream.Collectors;

@Configuration
public class JwtCustomizerConfig {

    static final String ROLES_CLAIM = "roles";
    static final String NAME_CLAIM = "name";
    static final String IMAGE_CLAIM = "image";
    static final String EMAIL_CLAIM = "email";

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if (context.getPrincipal() == null) {
                return;
            }

            final var authentication = context.getPrincipal();
            final var builder = context.getClaims();

            builder.claim(
                    ROLES_CLAIM,
                    authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList())
            );

            if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
                return;
            }

            final var authorizedScopes = context.getAuthorizedScopes();

            if (authorizedScopes.contains(OidcScopes.EMAIL)) {
                builder.claim(EMAIL_CLAIM, userDetails.getEmail());
            }

            if (authorizedScopes.contains(OidcScopes.PROFILE)) {
                builder.claim(NAME_CLAIM, userDetails.getName());

                if (userDetails.hasImage()) {
                    builder.claim(IMAGE_CLAIM, userDetails.getUsername());
                }
            }
        };
    }
}