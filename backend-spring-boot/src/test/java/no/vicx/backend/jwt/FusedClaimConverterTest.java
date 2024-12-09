package no.vicx.backend.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FusedClaimConverterTest {

    FusedClaimConverter sut;

    @BeforeEach
    void setUp() {
        sut = new FusedClaimConverter();
    }

    @Test
    void convert_givenFullyPopulatedJwt_expectTokenWithScopesAndRoles() {
        var authenticationToken =
                sut.convert(createJwtInTest(List.of("ROLE_ADMIN", "ROLE_USER")));

        assertNotNull(authenticationToken);
        assertThat(
                authoritiesAsStrings(authenticationToken),
                containsInAnyOrder("SCOPE_read", "SCOPE_write", "ROLE_ADMIN", "ROLE_USER"));
    }

    @Test
    void convert_givenRoleNotPrefixedWithRole_expectTokenWithPrefixedRole() {
        var authenticationToken =
                sut.convert(createJwtInTest(List.of("ROLE_ADMIN", "USER")));

        assertNotNull(authenticationToken);
        assertThat(
                authoritiesAsStrings(authenticationToken),
                allOf(hasItem("ROLE_ADMIN"), hasItem("ROLE_USER")));
    }

    @Test
    void convert_givenJwtWithoutRoles_expectTokenWithoutRoles() {
        var authenticationToken = sut.convert(createJwtInTest(null));

        assertNotNull(authenticationToken);
        assertThat(
                authoritiesAsStrings(authenticationToken),
                not(hasItem(startsWith("ROLE_"))));
    }

    private static Collection<String> authoritiesAsStrings(AbstractAuthenticationToken authenticationToken) {
        return authenticationToken.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    private static Jwt createJwtInTest(List<String> roles) {
        var builder = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("scope", "read write")
                .claim("roles", roles);

        if (roles != null) {
            builder.claim("roles", roles);
        }

        return builder.build();
    }
}