package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static no.vicx.authserver.config.JwtCustomizerConfig.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtCustomizerConfigTest {

    @Mock
    JwtEncodingContext context;

    @Mock
    Authentication authentication;

    @Mock
    CustomUserDetails customUserDetails;

    @Mock
    JwtClaimsSet.Builder claimsBuilder;

    OAuth2TokenCustomizer<JwtEncodingContext> sut;

    @BeforeEach
    void setUp() {
        sut = new JwtCustomizerConfig().jwtCustomizer();

        when(context.getPrincipal()).thenReturn(authentication);
    }

    @Test
    void customize_givenNullPrincipal_expectNoClaimsToBeSet() {
        when(context.getPrincipal()).thenReturn(null);

        sut.customize(context);

        verify(claimsBuilder, never()).claim(anyString(), any());
    }

    @Test
    void customize_givenContextForAccessToken_expectOnlyRolesClaimToBeSet() {
        when(context.getTokenType()).thenReturn(OAuth2TokenType.ACCESS_TOKEN);
        when(context.getClaims()).thenReturn(claimsBuilder);

        doReturn(List.of(
                new SimpleGrantedAuthority("USER"),
                new SimpleGrantedAuthority("ADMIN")
        )).when(authentication).getAuthorities();

        sut.customize(context);

        verify(claimsBuilder).claim(ROLES_CLAIM, List.of("USER", "ADMIN"));
        verify(claimsBuilder, never()).claim(eq(NAME_CLAIM), anyString());
        verify(claimsBuilder, never()).claim(eq(IMAGE_CLAIM), anyString());
        verify(claimsBuilder, never()).claim(eq(EMAIL_CLAIM), anyString());
    }

    @Test
    void customize_givenPrincipalDifferentFromCustomUserDetails_expectOnlyRolesClaimToBeSet() {
        when(context.getTokenType()).thenReturn(OAuth2TokenType.ACCESS_TOKEN);
        when(context.getClaims()).thenReturn(claimsBuilder);
        doReturn(List.of(new SimpleGrantedAuthority("USER"))).when(authentication).getAuthorities();

        sut.customize(context);

        verify(claimsBuilder).claim(ROLES_CLAIM, Collections.singletonList("USER"));
        verify(claimsBuilder, never()).claim(eq(NAME_CLAIM), anyString());
        verify(claimsBuilder, never()).claim(eq(IMAGE_CLAIM), anyString());
        verify(claimsBuilder, never()).claim(eq(EMAIL_CLAIM), anyString());
    }

    @Test
    void customize_givenNoScopes_expectOnlyRolesClaimToBeSet() {
        when(context.getTokenType()).thenReturn(OAuth2TokenType.ACCESS_TOKEN);
        when(context.getClaims()).thenReturn(claimsBuilder);
        doReturn(List.of(
                        new SimpleGrantedAuthority("USER"),
                        new SimpleGrantedAuthority("ADMIN")
                )).when(authentication).getAuthorities();

        sut.customize(context);

        verify(claimsBuilder).claim(ROLES_CLAIM, List.of("USER", "ADMIN"));

        verify(claimsBuilder, never()).claim(eq(NAME_CLAIM), anyString());
        verify(claimsBuilder, never()).claim(eq(IMAGE_CLAIM), anyString());
        verify(claimsBuilder, never()).claim(eq(EMAIL_CLAIM), anyString());
    }

    @Test
    void customize_givenUserDetailsWithoutImage_expectImageClaimNotToBeSet() {
        when(context.getAuthorizedScopes()).thenReturn(Set.of(OidcScopes.PROFILE, OidcScopes.EMAIL));
        when(context.getClaims()).thenReturn(claimsBuilder);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        doReturn(List.of(new SimpleGrantedAuthority("USER"))).when(authentication).getAuthorities();

        when(customUserDetails.hasImage()).thenReturn(false);
        when(customUserDetails.getName()).thenReturn("John Doe");
        when(customUserDetails.getEmail()).thenReturn("john.doe@example.com");
        when(customUserDetails.hasImage()).thenReturn(false);

        sut.customize(context);

        verify(claimsBuilder, never()).claim(eq(IMAGE_CLAIM), anyString());
    }

    @Test
    void customize_givenUserDetailsWithAllPropsSet_expectAllClaimsToBeSet() {
         when(context.getAuthorizedScopes()).thenReturn(Set.of(OidcScopes.PROFILE, OidcScopes.EMAIL));
         when(context.getClaims()).thenReturn(claimsBuilder);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        doReturn(List.of(
                new SimpleGrantedAuthority("USER"),
                new SimpleGrantedAuthority("ADMIN")
        )).when(authentication).getAuthorities();

         when(customUserDetails.getUsername()).thenReturn("john-doe");
         when(customUserDetails.getName()).thenReturn("John Doe");
         when(customUserDetails.getEmail()).thenReturn("john.doe@example.com");
         when(customUserDetails.hasImage()).thenReturn(true);

        sut.customize(context);

        verify(claimsBuilder).claim(ROLES_CLAIM, List.of("USER", "ADMIN"));
        verify(claimsBuilder).claim(NAME_CLAIM, "John Doe");
        verify(claimsBuilder).claim(IMAGE_CLAIM, "john-doe");
        verify(claimsBuilder).claim(EMAIL_CLAIM, "john.doe@example.com");
    }
}
