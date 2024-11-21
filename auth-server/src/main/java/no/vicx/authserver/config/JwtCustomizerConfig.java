package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.stream.Collectors;

@Configuration
public class JwtCustomizerConfig {

    public static final String PROFILE_SCOPE = "profile";
    public static final String ROLES_CLAIM = "roles";
    public static final String NAME_CLAIM = "name";
    public static final String EMAIL_CLAIM = "email";

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if (context.getPrincipal() instanceof Authentication authentication) {
                var authorities = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                context.getClaims().claim(ROLES_CLAIM, authorities);

                if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails
                        && context.getAuthorizedScopes().contains(PROFILE_SCOPE)) {

                    context.getClaims().claim(NAME_CLAIM, customUserDetails.getName());
                    context.getClaims().claim(EMAIL_CLAIM, customUserDetails.getEmail());
                }
            }
        };
    }
}