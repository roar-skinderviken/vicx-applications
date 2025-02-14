package no.vicx.backend.jwt.github.vm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitHubUserVmTest {

    @Test
    void isEmpty_givenEmptyUserVm_expectTrue() {
        assertTrue(new GitHubUserVm(null,null,null,null,null).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideNonEmptyUsers")
    void isEmpty_givenNonEmptyUserVm_expectFalse(GitHubUserVm userVm) {
        assertFalse(userVm.isEmpty());
    }

    private static Stream<Arguments> provideNonEmptyUsers() {
        return Stream.of(
                Arguments.of(
                        new GitHubUserVm(
                                "~id~", "~login~", "~user1~", "~email~", "~avatar~")),
                Arguments.of(
                        new GitHubUserVm(
                                null, "~login~", "~user1~", "~email~", "~avatar~")),
                Arguments.of(
                        new GitHubUserVm(
                                null, null, "~user1~", "~email~", "~avatar~")),
                Arguments.of(
                        new GitHubUserVm(
                                null, null, null, "~email~", "~avatar~")),
                Arguments.of(
                        new GitHubUserVm(
                                null, null, null, null, "~avatar~"))
        );
    }
}