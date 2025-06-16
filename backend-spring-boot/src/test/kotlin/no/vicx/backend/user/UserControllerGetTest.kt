package no.vicx.backend.user

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.vicx.backend.BaseWebMvcTest
import no.vicx.backend.error.NotFoundException
import no.vicx.backend.SecurityTestUtils
import no.vicx.backend.user.UserTestUtils.createValidVicxUser
import no.vicx.backend.user.service.UserService
import no.vicx.backend.user.vm.UserVm
import no.vicx.backend.user.vm.UserVm.Companion.fromVicxUser
import org.hamcrest.Matchers.`is`
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
class UserControllerGetTest(
    @MockkBean(relaxed = true) private val userService: UserService,
) : BaseWebMvcTest({

    Given("GET /api/user") {
        When("performing GET request without auth header") {
            val resultActions = mockMvc.perform(get("/api/user"))

            Then("expect Unauthorized") {
                resultActions.andExpect(status().isUnauthorized())
            }
        }

        When("performing GET request") {
            val resultActions = mockMvc.perform(
                get("/api/user")
                    .header(HttpHeaders.AUTHORIZATION, SecurityTestUtils.AUTH_HEADER_IN_TEST_GITHUB)
            )

            Then("expect Forbidden") {
                resultActions.andExpect(status().isForbidden())
            }
        }

        When("performing GET for non-existing user") {
            every { userService.getUserByUserName(any()) } throws NotFoundException("User user1 not found")

            val resultActions = mockMvc.perform(
                get("/api/user")
                    .header(HttpHeaders.AUTHORIZATION, SecurityTestUtils.AUTH_HEADER_IN_TEST)
            )

            Then("expect NotFound") {
                resultActions.andExpect(status().isNotFound())
            }
        }

        When("performing GET for existing user") {
            every { userService.getUserByUserName(any()) } returns createValidVicxUser()

            val resultActions = mockMvc.perform(
                get("/api/user")
                    .header(HttpHeaders.AUTHORIZATION, SecurityTestUtils.AUTH_HEADER_IN_TEST)
            )

            Then("expect OK") {
                resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", `is`("user1")))
                    .andExpect(jsonPath("$.name", `is`("The User")))
                    .andExpect(jsonPath("$.email", `is`("user@example.com")))
                    .andExpect(jsonPath("$.hasImage", `is`(false)))
            }
        }

        When("performing GET for existing user using WebTestClient") {
            val validUser = createValidVicxUser()
            every { userService.getUserByUserName(any()) } returns validUser

            webTestClient.get().uri("/api/user")
                .header(HttpHeaders.AUTHORIZATION, SecurityTestUtils.AUTH_HEADER_IN_TEST)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserVm::class.java).isEqualTo(fromVicxUser(validUser))
        }
    }
})