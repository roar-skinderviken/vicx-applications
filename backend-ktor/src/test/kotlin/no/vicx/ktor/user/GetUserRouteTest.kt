package no.vicx.ktor.user

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import no.vicx.ktor.error.ApiError
import no.vicx.ktor.user.UserTestConstants.API_USER
import no.vicx.ktor.user.vm.UserVm
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.RouteTestContext
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.SecurityTestUtils.tokenStringInTest

class GetUserRouteTest :
    BehaviorSpec({
        coroutineTestScope = true
        val routeTestContext = RouteTestContext()

        Given("mocked environment") {
            beforeContainer {
                clearAllMocks()
            }

            When("calling GET /api/user without authentication") {
                val response =
                    routeTestContext.runInTestApplicationContext { httpClient ->
                        httpClient.get(API_USER)
                    }

                Then("expect Unauthorized") {
                    response.status shouldBe HttpStatusCode.Unauthorized

                    coVerify { routeTestContext.userRepository wasNot called }
                }
            }

            When("calling GET /api/user with authentication and user in db") {
                coEvery { routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST) } returns userModelInTest

                val response =
                    routeTestContext.runInTestApplicationContext { httpClient ->
                        httpClient.get(API_USER) { bearerAuth(tokenStringInTest) }
                    }

                Then("expect OK") {
                    response.status shouldBe HttpStatusCode.OK

                    val userVm = response.body<UserVm>()

                    userVm shouldBe userModelInTest.toViewModel()

                    coVerify(exactly = 1) { routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST) }
                }
            }

            When("calling GET /api/user with authentication and no user in db") {
                coEvery { routeTestContext.userRepository.findByUsername(any()) } returns null

                val response =
                    routeTestContext.runInTestApplicationContext { httpClient ->
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
