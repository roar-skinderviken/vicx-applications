package no.vicx.user

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import no.vicx.error.ApiError
import no.vicx.user.UserTestConstants.API_USER
import no.vicx.util.RouteTestContext
import no.vicx.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.util.SecurityTestUtils.tokenStringInTest
import no.vicx.util.MiscTestUtils.userModelInTest

class GetUserRouteTest : BehaviorSpec({
    coroutineTestScope = true
    val routeTestContext = RouteTestContext()

    Given("mocked environment") {
        beforeContainer {
            clearAllMocks()
        }

        When("calling GET /api/user without authentication") {
            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.get(API_USER)
            }

            Then("expect Unauthorized") {
                response.status shouldBe HttpStatusCode.Unauthorized

                coVerify(exactly = 0) { routeTestContext.userRepository.findByUsername(any()) }
            }
        }

        When("calling GET /api/user with authentication and user in db") {
            coEvery { routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST) } returns userModelInTest

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.get(API_USER) { bearerAuth(tokenStringInTest) }
            }

            Then("expect OK") {
                response.status shouldBe HttpStatusCode.OK
                coVerify(exactly = 1) { routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST) }
            }
        }

        When("calling GET /api/user with authentication and no user in db") {
            coEvery { routeTestContext.userRepository.findByUsername(any()) } returns null

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.get(API_USER) { bearerAuth(tokenStringInTest) }
            }

            Then("expect NotFound") {
                response.status shouldBe HttpStatusCode.NotFound
                response.body<ApiError>().message shouldBe "User $USERNAME_IN_TEST not found"

                coVerify(exactly = 1) { routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST) }
            }
        }
    }
})