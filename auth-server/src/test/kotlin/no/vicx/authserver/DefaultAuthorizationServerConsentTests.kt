package no.vicx.authserver

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import no.vicx.authserver.HtmlUnitTestUtils.REDIRECT_URI
import no.vicx.authserver.HtmlUnitTestUtils.authorizationRequestUri
import no.vicx.authserver.HtmlUnitTestUtils.withMockUser
import org.htmlunit.Page
import org.htmlunit.WebClient
import org.htmlunit.html.DomElement
import org.htmlunit.html.HtmlCheckBoxInput
import org.htmlunit.html.HtmlPage
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DefaultAuthorizationServerConsentTests(
    webClient: WebClient,
    @MockkBean(relaxed = true) private val authorizationConsentService: OAuth2AuthorizationConsentService
) : BehaviorSpec({

    Given("Authorization Server Application") {
        lateinit var consentPage: HtmlPage

        beforeContainer {
            with(webClient) {
                options.isThrowExceptionOnFailingStatusCode = false
                options.isRedirectEnabled = false
                cookieManager.clearCookies() // log out
            }

            withMockUser("user1")

            consentPage = webClient.getPage(
                authorizationRequestUri("openid profile email")
            )
        }

        When("consent page is displayed") {
            val titleText = consentPage.titleText

            val scopesInPage = consentPage
                .querySelectorAll("input[name='scope']")
                .filterIsInstance<HtmlCheckBoxInput>()
                .map { it.id }

            Then("title text should be 'Consent required'") {
                titleText shouldBe "Consent required"
            }

            Then("scopes should be as expected") {
                scopesInPage.shouldContainExactlyInAnyOrder("profile", "email")
            }
        }

        When("user consents to all scopes") {
            consentPage
                .querySelectorAll("input[name='scope']")
                .filterIsInstance<HtmlCheckBoxInput>()
                .forEach { checkBox -> checkBox.click<Page>() }

            val approveConsentResponse = consentPage
                .querySelector<DomElement>("button[id='submit-consent']")
                .click<Page>()
                .webResponse

            Then("approve response should be as expected") {
                assertSoftly(approveConsentResponse) {
                    statusCode shouldBe HttpStatus.MOVED_PERMANENTLY.value()

                    assertSoftly(getResponseHeaderValue("location")) {
                        it shouldStartWith REDIRECT_URI
                        it shouldContain "code="
                    }
                }
            }
        }

        When("user cancels consent") {
            val location = consentPage
                .querySelector<DomElement>("button[id='cancel-consent']")
                .click<Page>()
                .webResponse
                .getResponseHeaderValue("location")

            Then("expect access denied error") {
                assertSoftly(location) {
                    it shouldStartWith REDIRECT_URI
                    it shouldContain "error=access_denied"
                }
            }
        }
    }
})