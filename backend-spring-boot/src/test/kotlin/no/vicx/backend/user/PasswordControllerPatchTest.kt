package no.vicx.backend.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.data.Row3
import io.kotest.data.forAll
import io.mockk.every
import no.vicx.backend.BaseWebMvcTest
import no.vicx.backend.SecurityTestUtils.AUTH_HEADER_IN_TEST
import no.vicx.backend.SecurityTestUtils.AUTH_HEADER_IN_TEST_GITHUB
import no.vicx.backend.user.service.UserService
import no.vicx.backend.user.vm.ChangePasswordVm
import no.vicx.database.user.VicxUser
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PasswordController::class)
class PasswordControllerPatchTest(
    @MockkBean(relaxed = true) private val userService: UserService,
) : BaseWebMvcTest({

    Given("PATCH /api/user/password") {
        When("performing patch request without auth header") {
            val resultActions = mockMvc.perform(patch("/api/user/password"))

            Then("expect Unauthorized") {
                resultActions.andExpect(status().isUnauthorized)
            }
        }

        When("performing patch request with invalid existing password") {
            every { userService.isValidPassword(any(), any()) } returns false

            val body = ChangePasswordVm("~current-password~", VicxUser.VALID_PLAINTEXT_PASSWORD)

            val resultActions = mockMvc.perform(createValidChangePasswordRequest(body))

            Then("expect BadRequest and validation error") {
                resultActions
                    .andExpect(status().isBadRequest)
                    .andExpect(
                        jsonPath("$.validationErrors.currentPassword")
                            .value("Incorrect current password, please try again")
                    )
            }
        }

        When("performing patch request with valid request") {
            every { userService.isValidPassword(any(), any()) } returns true

            val body = ChangePasswordVm("~current-password~", VicxUser.VALID_PLAINTEXT_PASSWORD)

            val resultActions = mockMvc.perform(createValidChangePasswordRequest(body))

            Then("expect OK") {
                resultActions
                    .andExpect(status().isOk)
                    .andExpect(content().string("Your password has been successfully updated."))
            }
        }

        When("performing patch request without required role") {
            val resultActions = mockMvc.perform(
                patch("/api/user/password")
                    .content("{}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST_GITHUB)
            )

            Then("expect Forbidden") {
                resultActions.andExpect(status().isForbidden)
            }
        }

        forAll(
            Row3(ChangePasswordVm(null, "Aa1Aa1Aa"), "currentPassword", "Cannot be null"),
            Row3(
                ChangePasswordVm("", "Aa1Aa1Aa"),
                "currentPassword",
                "It must have minimum 4 and maximum 255 characters"
            ),
            Row3(
                ChangePasswordVm("Aa1", "Aa1Aa1Aa"),
                "currentPassword",
                "It must have minimum 4 and maximum 255 characters"
            ),
            Row3(
                ChangePasswordVm("Aa1".repeat(90), "Aa1Aa1Aa"),
                "currentPassword",
                "It must have minimum 4 and maximum 255 characters"
            ),

            Row3(ChangePasswordVm("~current-password~", null), "password", "Cannot be null"),
            Row3(
                ChangePasswordVm("~current-password~", "Aa1Aa1"),
                "password",
                "It must have minimum 8 and maximum 255 characters"
            ),
            Row3(
                ChangePasswordVm("~current-password~", "Aa1".repeat(90)),
                "password",
                "It must have minimum 8 and maximum 255 characters"
            ),
            Row3(
                ChangePasswordVm("~current-password~", "a".repeat(8)),
                "password",
                "Password must have at least one uppercase, one lowercase letter and one number"
            )
        ) { body, field, expectedError ->
            When("performing invalid patch request: $body $field") {
                val resultActions = mockMvc.perform(createValidChangePasswordRequest(body))

                Then("expect BadRequest and validation error") {
                    resultActions
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.validationErrors.$field").value(expectedError))
                }
            }
        }
    }
}) {
    companion object {
        private val objectMapper = ObjectMapper()

        private fun createValidChangePasswordRequest(
            content: ChangePasswordVm
        ) = createValidChangePasswordRequest(objectMapper.writeValueAsString(content))

        private fun createValidChangePasswordRequest(
            content: String,
        ) = patch("/api/user/password")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST)
    }
}