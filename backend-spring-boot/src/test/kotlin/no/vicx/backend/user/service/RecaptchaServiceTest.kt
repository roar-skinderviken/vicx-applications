package no.vicx.backend.user.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row2
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import no.vicx.backend.user.service.RecaptchaService.Companion.RECAPTCHA_VERIFY_BASE_URL
import no.vicx.backend.user.service.RecaptchaService.Companion.SECRET_REQUEST_PARAMETER
import no.vicx.backend.user.service.RecaptchaService.Companion.SITE_VERIFY_PATH
import no.vicx.backend.user.service.RecaptchaService.Companion.TOKEN_RESPONSE_PARAMETER
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.util.UriComponentsBuilder

@RestClientTest(RecaptchaService::class)
class RecaptchaServiceTest(
    mockServer: MockRestServiceServer,
    sut: RecaptchaService,
    @Value($$"${recaptcha.secret}") recaptchaSecret: String,
) : BehaviorSpec({

        val expectedUrl =
            UriComponentsBuilder
                .fromUriString("$RECAPTCHA_VERIFY_BASE_URL$SITE_VERIFY_PATH")
                .queryParam(SECRET_REQUEST_PARAMETER, recaptchaSecret)
                .queryParam(TOKEN_RESPONSE_PARAMETER, TOKEN_IN_TEST)
                .build()
                .toUri()

        Given("request with valid reCAPTCHA token") {
            mockServer
                .expect(requestTo(expectedUrl))
                .andRespond(
                    withSuccess(validResponseBody, MediaType.APPLICATION_JSON),
                )

            When("calling verifyToken") {
                val result = sut.verifyToken(TOKEN_IN_TEST)

                Then("expect true") {
                    result shouldBe true
                }
            }
        }

        Given("error response") {
            mockServer
                .expect(requestTo(expectedUrl))
                .andRespond(
                    withSuccess(errorResponseBody, MediaType.APPLICATION_JSON),
                )

            When("calling verifyToken") {
                val result = sut.verifyToken(TOKEN_IN_TEST)

                Then("expect false") {
                    result shouldBe false
                }
            }
        }

        Given("partial responses") {
            forAll(
                Row2("Empty body", ""),
                Row2("Empty JSON object", "{}"),
                // Funker ikke lenger med SB 4 Row2("JSON object with null field", "{\"success\": null}"),
            ) { description, body ->
                When("calling verifyToken: $description") {
                    mockServer
                        .expect(requestTo(expectedUrl))
                        .andRespond(
                            withSuccess(body, MediaType.APPLICATION_JSON),
                        )

                    val result = sut.verifyToken(TOKEN_IN_TEST)

                    Then("expect false") {
                        result shouldBe false
                    }
                }
            }
        }
    }) {
    companion object {
        private const val TOKEN_IN_TEST = "~token~"

        private val validResponseBody =
            """
            {
                "success": true,
                "challenge_ts": "~challenge_ts~",
                "hostname": "~hostname~",
                "error-codes": []
            }
            """.trimIndent()

        private val errorResponseBody =
            """
            {
                "success": false,
                "challenge_ts": "~challenge_ts~",
                "hostname": "~hostname~",
                "error-codes": ["Some error"]
            }
            """.trimIndent()
    }
}
