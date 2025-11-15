package no.vicx.backend.user.vm

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.json.JsonTest
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue

@JsonTest
class RecaptchaResponseVmTest(
    objectMapper: ObjectMapper,
) : BehaviorSpec({
        Given("valid reCAPTCHA response JSON") {
            val json =
                """
                {
                    "success": true,
                    "challenge_ts": "2024-12-09T14:30:00+00:00",
                    "hostname": "~hostname~",
                    "error-codes": ["~error-code1~","~error-code2~"]
                }
                """.trimIndent()

            When("mapping JSON to view model") {
                val viewModel = objectMapper.readValue<RecaptchaResponseVm>(json)

                Then("view model should be as expected") {
                    assertSoftly(viewModel) {
                        success shouldBe true
                        challengeTimestamp shouldBe "2024-12-09T14:30:00+00:00"
                        hostname shouldBe "~hostname~"
                        errorCodes shouldBe listOf("~error-code1~", "~error-code2~")
                    }
                }
            }
        }
    })
