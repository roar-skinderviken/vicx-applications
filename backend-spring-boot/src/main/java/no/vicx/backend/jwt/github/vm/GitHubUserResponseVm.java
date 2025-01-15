package no.vicx.backend.jwt.github.vm;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collections;
import java.util.Map;

public record GitHubUserResponseVm(
        GitHubUserVm user,
        String grantedScopes,
        String token) {

    public OAuth2AuthenticatedPrincipal toPrincipal() {
        return new DefaultOAuth2AuthenticatedPrincipal(
                user.login(),
                Map.of("sub", user.login()),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_GITHUB_USER"))
        );
    }
}
