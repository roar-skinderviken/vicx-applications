package no.vicx.backend.jwt.github.vm;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GitHubUserResponseVmTest {

    @Test
    void toPrincipal_givenUserWithAllFieldsSet_expectPrincipalWithAllFieldsSet() {
        var sut = new GitHubUserResponseVm(
                USER_IN_TEST, "~granted-scopes~", "~token~");

        var principal = sut.toPrincipal();

        assertNotNull(principal);
        assertEquals("~login~", principal.getName());
        assertThat(
                principal.getAuthorities(),
                contains(new SimpleGrantedAuthority("ROLE_GITHUB_USER")));
    }

    private static final GitHubUserVm USER_IN_TEST = new GitHubUserVm(
            "~id~", "~login~", "~name~", "~email~", "~avatar~");
}