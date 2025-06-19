package no.vicx.authserver

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import no.vicx.authserver.HtmlUnitTestUtils.REDIRECT_URI
import no.vicx.authserver.HtmlUnitTestUtils.authorizationRequestUri
import org.htmlunit.Page
import org.htmlunit.WebClient
import org.htmlunit.html.DomNode
import org.htmlunit.html.HtmlButton
import org.htmlunit.html.HtmlInput
import org.htmlunit.html.HtmlPage
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DefaultAuthorizationServerApplicationTest(
    webClient: WebClient
) : BehaviorSpec({

    Given("Authorization Server Application") {

        beforeContainer {
            webClient.options.isThrowExceptionOnFailingStatusCode = true
            webClient.options.isRedirectEnabled = true
            webClient.cookieManager.clearCookies() // log out
        }

        When("login successful") {
            val htmlPage = webClient.getPage<HtmlPage>("/")
            assertLoginPage(htmlPage)

            webClient.options.isThrowExceptionOnFailingStatusCode = false
            val signInResponse = signIn<Page>(htmlPage, "password").webResponse

            Then("display NotFound") {
                signInResponse.statusCode shouldBe HttpStatus.NOT_FOUND.value()
            }
        }

        When("login fails") {
            val page = webClient.getPage<HtmlPage>("/")

            val loginErrorPage = signIn<HtmlPage>(page, "wrong-password")

            Then("expect invalid credentials message") {
                loginErrorPage
                    .querySelector<DomNode>("div[role=\"alert\"]")
                    .textContent shouldBe "Invalid credentials"
            }
        }

        When("not logged in and requesting token") {
            val page = webClient.getPage<HtmlPage>(authorizationRequestUri())

            Then("expect login page") {
                assertLoginPage(page)
            }
        }

        When("when logging in and requesting token") {
            // Log in
            webClient.options.isThrowExceptionOnFailingStatusCode = false
            webClient.options.isRedirectEnabled = false

            signIn<Page>(webClient.getPage("/login"), "password")

            // Request token
            val response = webClient.getPage<Page>(authorizationRequestUri()).webResponse

            Then("redirects to client application") {
                response.statusCode shouldBe HttpStatus.MOVED_PERMANENTLY.value()

                val location = response.getResponseHeaderValue("location")
                location shouldStartWith REDIRECT_URI
                location shouldContain "code="
            }
        }
    }
}) {
    companion object {
        private fun <P : Page> signIn(
            page: HtmlPage,
            password: String
        ): P {
            val usernameInput = page.querySelector<HtmlInput>("input[name=\"username\"]")
            val passwordInput = page.querySelector<HtmlInput>("input[name=\"password\"]")
            val signInButton = page.querySelector<HtmlButton>("button")

            usernameInput.type("user1")
            passwordInput.type(password)

            return signInButton.click()
        }

        private fun assertLoginPage(page: HtmlPage) {
            page.url.toString() shouldEndWith "/login"

            page.querySelector<HtmlInput>("input[name=\"username\"]").shouldNotBeNull()
            page.querySelector<HtmlInput>("input[name=\"password\"]").shouldNotBeNull()
            page.querySelector<DomNode>("button").textContent shouldBe "Sign in"
        }
    }
}