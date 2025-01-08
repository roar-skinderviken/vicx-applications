package no.vicx.backend.jwt.github.vm;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class GitHubUserResponseVmTest {

    static GitHubUserVm userInTest = new GitHubUserVm(
            "~id~", "~login~", "~name~", "~email~", "~avatar~");

    @Test
    void toJwt_givenUserWithAllFieldsSet_expectJwtWithAllFieldsSet() {
        var sut = new GitHubUserResponseVm(
                userInTest, "~granted-scopes~", "~token~");

        var principal = sut.toPrincipal();

        assertNotNull(principal);
        assertEquals("~login~", principal.getName());
        assertEquals(
                Collections.singletonList(new SimpleGrantedAuthority("GITHUB_USER")),
                principal.getAuthorities());
    }
}