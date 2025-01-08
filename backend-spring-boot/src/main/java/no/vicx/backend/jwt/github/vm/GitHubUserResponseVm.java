package no.vicx.backend.jwt.github.vm;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.*;

public record GitHubUserResponseVm(
        GitHubUserVm user,
        String grantedScopes,
        String token) {

    public OAuth2AuthenticatedPrincipal toPrincipal() {
        return new OAuth2AuthenticatedPrincipal() {
            @Override
            public Map<String, Object> getAttributes() {
                return Collections.emptyMap();
            }

            @Override
            public Collection<GrantedAuthority> getAuthorities() {
                return Collections.singletonList(
                        new SimpleGrantedAuthority("GITHUB_USER"));
            }

            @Override
            public String getName() {
                return user.login();
            }
        };
    }
}
