package no.vicx.backend.jwt.github.vm;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertThat(
                principal.getAuthorities(),
                containsInAnyOrder(new SimpleGrantedAuthority("ROLE_GITHUB_USER")));
    }
}