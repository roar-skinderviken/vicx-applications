package no.vicx.authserver.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OAuthPropertiesTest {

    private static final ApplicationContextRunner CONTEXT_RUNNER = new ApplicationContextRunner();

    @EnableConfigurationProperties(OAuthProperties.class)
    static class OAuthPropertiesTestConfig {
    }

    @Test
    void contextLoad_givenValidOAuthProperties_expectContextToLoad() {
        CONTEXT_RUNNER
                .withUserConfiguration(OAuthPropertiesTestConfig.class)
                .withPropertyValues(
                        "oauth.client-id=~client-id~",
                        "oauth.client-secret=~client-secret~",
                        "oauth.redirect-uri=~redirect-uri~",
                        "oauth.post-logout-redirect-uri=~post-logout-redirect-uri~",
                        "oauth.access-token-time-to-live=PT5M",
                        "oauth.refresh-token-time-to-live=PT1H"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(OAuthProperties.class);

                    var sut = context.getBean(OAuthProperties.class);

                    assertEquals("~client-id~", sut.clientId());
                    assertEquals("~client-secret~", sut.clientSecret());
                    assertEquals("~redirect-uri~", sut.redirectUri());
                    assertEquals("~post-logout-redirect-uri~", sut.postLogoutRedirectUri());
                    assertEquals(Duration.ofMinutes(5), sut.accessTokenTimeToLive());
                    assertEquals(Duration.ofHours(1), sut.refreshTokenTimeToLive());
                });
    }

    @ParameterizedTest
    @MethodSource("invalidParametersSource")
    void contextLoad_givenInvalidOAuthProperties_expectContextLoadToFail(
            String clientId, String clientSecret, String redirectUri, String postLogoutRedirectUri,
            String accessTokenTimeToLive, String refreshTokenTimeToLive) {

        CONTEXT_RUNNER
                .withUserConfiguration(OAuthPropertiesTestConfig.class)
                .withPropertyValues(
                        "oauth.client-id=" + clientId,
                        "oauth.client-secret=" + clientSecret,
                        "oauth.redirect-uri=" + redirectUri,
                        "oauth.post-logout-redirect-uri=" + postLogoutRedirectUri,
                        "oauth.access-token-time-to-live=" + accessTokenTimeToLive,
                        "oauth.refresh-token-time-to-live=" + refreshTokenTimeToLive
                )
                .run(context -> assertThat(context).hasFailed());
    }

    static Stream<Arguments> invalidParametersSource() {
        return Stream.of(
                Arguments.of(" ", "~client-secret~", "~redirect-uri~", "~post-logout-redirect-uri~",
                        "PT5M", "PT1H"),
                Arguments.of("~client-id~", " ", "~redirect-uri~", "~post-logout-redirect-uri~",
                        "PT5M", "PT1H"),
                Arguments.of("~client-id~", "~client-secret~", " ", "~post-logout-redirect-uri~",
                        "PT5M", "PT1H"),
                Arguments.of("~client-id~", "~client-secret~", "~redirect-uri~", " ",
                        "PT5M", "PT1H"),
                Arguments.of("~client-id~", "~client-secret~", "~redirect-uri~", "~post-logout-redirect-uri~",
                        " ", "PT1H"),
                Arguments.of("~client-id~", "~client-secret~", "~redirect-uri~", "~post-logout-redirect-uri~",
                        "PT5M", " "));
    }
}