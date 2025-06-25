package no.vicx.backend.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.data.Row1
import io.kotest.data.Row3
import io.kotest.data.forAll
import io.mockk.verify
import no.vicx.backend.BaseWebMvcTest
import no.vicx.backend.SecurityTestUtils
import no.vicx.backend.user.service.UserService
import no.vicx.backend.user.vm.UserPatchVm
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
class UserControllerPatchTest(
    @MockkBean(relaxed = true) private val userService: UserService,
) : BaseWebMvcTest({

        Given("PATCH /api/user") {
            When("performing patch request without auth header") {
                val resultActions = mockMvc.perform(patch("/api/user"))

                Then("expect Unauthorized") {
                    resultActions.andExpect(status().isUnauthorized)
                }
            }

            When("performing patch request without required role") {
                val resultActions =
                    mockMvc.perform(
                        patch("/api/user")
                            .content("{}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, SecurityTestUtils.AUTH_HEADER_IN_TEST_GITHUB),
                    )

                Then("expect Forbidden") {
                    resultActions.andExpect(status().isForbidden)
                }
            }

            When("performing patch request with empty body") {
                val resultActions = mockMvc.perform(createValidCPatchRequest("{}"))

                Then("expect BadRequest") {
                    resultActions.andExpect(status().isBadRequest)
                }
            }

            forAll(
                Row1(UserPatchVm("~name~", "foo@bar.com")),
                Row1(UserPatchVm("~name~", null)),
                Row1(UserPatchVm(null, "foo@bar.com")),
            ) { userPatchVm ->

                When("performing patch request with valid body: $userPatchVm") {
                    val resultActions = mockMvc.perform(createValidCPatchRequest(userPatchVm))

                    Then("expect OK") {
                        resultActions.andExpect(status().isOk)

                        verify { userService.updateUser(userPatchVm, "user1") }
                    }
                }
            }

            forAll(
                Row3(UserPatchVm(null, null), "patchRequestBody", "At least one field must be provided"),
                Row3(UserPatchVm("a".repeat(3), null), "name", "It must have minimum 4 and maximum 255 characters"),
                Row3(UserPatchVm("a".repeat(256), null), "name", "It must have minimum 4 and maximum 255 characters"),
                Row3(UserPatchVm(null, "a"), "email", "It must be a well-formed email address"),
            ) { userPatchVm, field, expectedError ->
                When("performing invalid patch request: $userPatchVm $field") {
                    val resultActions = mockMvc.perform(createValidCPatchRequest(userPatchVm))

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

        private fun createValidCPatchRequest(content: UserPatchVm) = createValidCPatchRequest(objectMapper.writeValueAsString(content))

        private fun createValidCPatchRequest(content: String) =
            patch("/api/user")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, SecurityTestUtils.AUTH_HEADER_IN_TEST)
    }
}
