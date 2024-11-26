package no.vicx.backend.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FusedClaimConverterTest {

    FusedClaimConverter sut;

    @BeforeEach
    void setUp() {
        sut = new FusedClaimConverter();
    }

    @Test
    void convert_givenFullyPopulatedJwt_expectAuthenticationToken() {
        var authenticationToken =
                sut.convert(createJwt(List.of("ROLE_ADMIN", "ROLE_USER")));

        assertNotNull(authenticationToken);
        assertTrue(hasAuthorities(authenticationToken,
                "SCOPE_read", "SCOPE_write", "ROLE_ADMIN", "ROLE_USER"));
    }

    @Test
    void convert_givenRoleNonPrefixedRole_expectPrefixedRole() {
        var authenticationToken =
                sut.convert(createJwt(List.of("ADMIN", "ROLE_USER")));

        assertNotNull(authenticationToken);
        assertTrue(hasAuthorities(authenticationToken, "ROLE_ADMIN", "ROLE_USER"));
    }

    @Test
    void convert_givenJwtWithoutRoles_expectAuthenticationToken() {
        var authenticationToken = sut.convert(createJwt(null));

        assertNotNull(authenticationToken);

        assertTrue(authenticationToken.getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().startsWith("ROLE_")));
    }

    private static boolean hasAuthorities(
            AbstractAuthenticationToken authenticationToken,
            String... expectedAuth) {

        var actualAuth = authenticationToken.getAuthorities();

        return Arrays.stream(expectedAuth)
                .allMatch(it -> actualAuth.contains(new SimpleGrantedAuthority(it)));
    }

    private static Jwt createJwt(List<String> roles) {
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