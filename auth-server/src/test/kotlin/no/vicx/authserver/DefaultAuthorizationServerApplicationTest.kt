package no.vicx.authserver

import io.kotest.assertions.assertSoftly
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
    webClient: WebClient,
) : BehaviorSpec({

        Given("Authorization Server Application") {
            lateinit var loginPage: HtmlPage

            beforeContainer {
                with(webClient) {
                    options.isThrowExceptionOnFailingStatusCode = true
                    options.isRedirectEnabled = true
                    cookieManager.clearCookies() // log out
                }

                loginPage = webClient.getPage("/")
                assertLoginPage(loginPage)
            }

            When("login successful") {
                webClient.options.isThrowExceptionOnFailingStatusCode = false

                val signInResponse = signIn<Page>(loginPage, "password").webResponse

                Then("display NotFound") {
                    signInResponse.statusCode shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            When("login fails") {
                val loginErrorPage = signIn<HtmlPage>(loginPage, "wrong-password")

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
                with(webClient) {
                    options.isThrowExceptionOnFailingStatusCode = false
                    options.isRedirectEnabled = false
                }

                signIn<Page>(webClient.getPage("/login"), "password")

                // Request token
                val loginResponse = webClient.getPage<Page>(authorizationRequestUri()).webResponse

                Then("redirects to client application") {
                    assertSoftly(loginResponse) {
                        statusCode shouldBe HttpStatus.MOVED_PERMANENTLY.value()

                        assertSoftly(getResponseHeaderValue("location")) {
                            it shouldStartWith REDIRECT_URI
                            it shouldContain "code="
                        }
                    }
                }
            }
        }
    }) {
    companion object {
        private fun <P : Page> signIn(
            page: HtmlPage,
            password: String,
        ): P =
            page.run {
                querySelector<HtmlInput>("input[name=\"username\"]").type("user1")
                querySelector<HtmlInput>("input[name=\"password\"]").type(password)
                querySelector<HtmlButton>("button").click()
            }

        private fun assertLoginPage(page: HtmlPage) {
            assertSoftly(page) {
                url.toString() shouldEndWith "/login"

                querySelector<HtmlInput>("input[name=\"username\"]").shouldNotBeNull()
                querySelector<HtmlInput>("input[name=\"password\"]").shouldNotBeNull()
                querySelector<DomNode>("button").textContent shouldBe "Sign in"
            }
        }
    }
}
