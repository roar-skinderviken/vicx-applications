package no.vicx.backend.jwt.github.vm;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static no.vicx.backend.jwt.JwtConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class GitHubUserResponseVmTest {

    static GitHubUserVm userInTest = new GitHubUserVm(
            "~id~", "~login~", "~name~", "~email~", "~avatar~");

    static GitHubUserVm minimalUser = new GitHubUserVm(
            "~id~", "~login~", null, null, null);

    static GitHubUserVm minimalUserWithBlankEmail = new GitHubUserVm(
            "~id~", "~login~", null, " ", null);

    @Test
    void toJwt_givenUserWithAllFieldsSet_expectJwtWithAllFieldsSet() {
        var sut = new GitHubUserResponseVm(
                userInTest, "~granted-scopes~", null, "~token~");

        var jwt = sut.toJwt();

        assertNotNull(jwt);
        assertNotNull(jwt.getIssuedAt());
        assertNotNull(jwt.getExpiresAt());
        assertEquals(Collections.singletonMap(HEADER_ALG, HEADER_ALG_NONE), jwt.getHeaders());

        assertEquals("~login~", jwt.getSubject());
        assertEquals("~granted-scopes~", jwt.getClaim(CLAIM_SCOPES));
        assertEquals(Collections.singletonList("ROLE_GITHUB_USER"), jwt.getClaim(CLAIM_ROLES));
        assertEquals("~email~", jwt.getClaim(CLAIM_EMAIL));
        assertEquals("~name~", jwt.getClaim(CLAIM_NAME));
        assertEquals("~avatar~", jwt.getClaim(CLAIM_IMAGE));
    }

    @Test
    void toJwt_givenUserWithMinimalInfo_expectJwt() {
        var sut = new GitHubUserResponseVm(
                minimalUser, "~granted-scopes~",
                "~additionalEmailAddress~", "~token~");

        var jwt = sut.toJwt();

        assertEquals("~additionalEmailAddress~", jwt.getClaim(CLAIM_EMAIL));
        assertNull(jwt.getClaim(CLAIM_NAME));
        assertNull(jwt.getClaim(CLAIM_IMAGE));
    }

    @Test
    void toJwt_givenUserWithBlankEmail_expectJwtWithAdditionalEmailAddress() {
        var sut = new GitHubUserResponseVm(
                minimalUserWithBlankEmail, "~granted-scopes~",
                "~additionalEmailAddress~", "~token~");

        var jwt = sut.toJwt();

        assertEquals("~additionalEmailAddress~", jwt.getClaim(CLAIM_EMAIL));
    }
}