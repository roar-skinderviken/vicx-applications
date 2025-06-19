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
            webClient.options.isThrowExceptionOnFailingStatusCode = false
            webClient.options.isRedirectEnabled = false
            webClient.cookieManager.clearCookies() // log out

            withMockUser("user1")

            consentPage = webClient.getPage(
                authorizationRequestUri("openid profile email")
            )
        }

        When("user consents to all scopes") {
            val scopesInPage = consentPage
                .querySelectorAll("input[name='scope']")
                .filterIsInstance<HtmlCheckBoxInput>()
                .map { checkBox ->
                    checkBox.click<Page>()
                    checkBox.id
                }

            val approveConsentResponse = consentPage
                .querySelector<DomElement>("button[id='submit-consent']")
                .click<Page>()
                .webResponse

            Then("title text should be 'Consent required'") {
                consentPage.titleText shouldBe "Consent required"
            }

            Then("scopes should be as expected") {
                scopesInPage.shouldContainExactlyInAnyOrder("profile", "email")
            }

            Then("approve response should be as expected") {
                assertSoftly(approveConsentResponse) {
                    statusCode shouldBe HttpStatus.MOVED_PERMANENTLY.value()

                    val location = getResponseHeaderValue("location")
                    location shouldStartWith REDIRECT_URI
                    location shouldContain "code="
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
                location shouldStartWith REDIRECT_URI
                location shouldContain "error=access_denied"
            }
        }
    }
})