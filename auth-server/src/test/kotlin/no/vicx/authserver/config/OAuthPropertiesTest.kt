package no.vicx.authserver.config

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row7
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import no.vicx.authserver.config.DefaultUserPropertiesTest.DefaultUserPropertiesTestConfig
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.springframework.beans.factory.getBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import java.time.Duration

class OAuthPropertiesTest : BehaviorSpec({

    Given("context with all properties") {
        val contextRunner = CONTEXT_RUNNER
            .withUserConfiguration(OAuthPropertiesTestConfig::class.java)
            .withPropertyValues(
                "oauth.client-id=~client-id~",
                "oauth.client-secret=~client-secret~",
                "oauth.redirect-uri=~redirect-uri~",
                "oauth.post-logout-redirect-uri=~post-logout-redirect-uri~",
                "oauth.resource-server=~resource-server~",
                "oauth.access-token-time-to-live=PT5M",
                "oauth.refresh-token-time-to-live=PT1H"
            )

        Then("expect context to load and properties to be populated") {
            contextRunner.run { context: AssertableApplicationContext ->
                assertThat(context).hasSingleBean(OAuthProperties::class.java)
                val oAuthProperties = context.getBean<OAuthProperties>()

                assertSoftly(oAuthProperties) {
                    clientId shouldBe "~client-id~"
                    clientSecret shouldBe "~client-secret~"
                    redirectUri shouldBe "~redirect-uri~"
                    postLogoutRedirectUri shouldBe "~post-logout-redirect-uri~"
                    resourceServer shouldBe "~resource-server~"
                    accessTokenTimeToLive shouldBe Duration.ofMinutes(5)
                    refreshTokenTimeToLive shouldBe Duration.ofHours(1)
                }
            }
        }
    }

    Given("context with invalid properties") {
        forAll(
            Row7(
                " ",
                "~client-secret~",
                "~redirect-uri~",
                "~resource-server~",
                "~post-logout-redirect-uri~",
                "PT5M",
                "PT1H"
            ),
            Row7(
                "~client-id~",
                " ",
                "~redirect-uri~",
                "~resource-server~",
                "~post-logout-redirect-uri~",
                "PT5M",
                "PT1H"
            ),
            Row7(
                "~client-id~",
                "~client-secret~",
                " ",
                "~resource-server~",
                "~post-logout-redirect-uri~",
                "PT5M",
                "PT1H"
            ),
            Row7("~client-id~", "~client-secret~", "~redirect-uri~", " ", "~post-logout-redirect-uri~", "PT5M", "PT1H"),
            Row7("~client-id~", "~client-secret~", "~redirect-uri~", "~resource-server~", " ", "PT5M", "PT1H"),
            Row7(
                "~client-id~",
                "~client-secret~",
                "~redirect-uri~",
                "~resource-server~",
                "~post-logout-redirect-uri~",
                " ",
                "PT1H"
            ),
            Row7(
                "~client-id~",
                "~client-secret~",
                "~redirect-uri~",
                "~resource-server~",
                "~post-logout-redirect-uri~",
                "PT5M",
                " "
            )
        ) { clientId, clientSecret, redirectUri, resourceServer,
            postLogoutRedirectUri, accessTokenExpiry, refreshTokenExpiry ->

            val contextRunner = CONTEXT_RUNNER
                .withUserConfiguration(DefaultUserPropertiesTestConfig::class.java)
                .withPropertyValues(
                    "oauth.client-id=$clientId",
                    "oauth.client-secret=$clientSecret",
                    "oauth.redirect-uri=$redirectUri",
                    "oauth.post-logout-redirect-uri=$postLogoutRedirectUri",
                    "oauth.resource-server=$resourceServer",
                    "oauth.access-token-time-to-live=$accessTokenExpiry",
                    "oauth.refresh-token-time-to-live=$refreshTokenExpiry"
                )
            Then(
                "expect context to fail loading, $clientId, \$clientSecret, \$redirectUri, " +
                        "$resourceServer, $postLogoutRedirectUri, $accessTokenExpiry, $refreshTokenExpiry"
            ) {
                contextRunner.run { context -> assertThat(context).hasFailed() }
            }
        }
    }
}) {
    @EnableConfigurationProperties(OAuthProperties::class)
    class OAuthPropertiesTestConfig

    companion object {
        private val CONTEXT_RUNNER = ApplicationContextRunner()
    }
}