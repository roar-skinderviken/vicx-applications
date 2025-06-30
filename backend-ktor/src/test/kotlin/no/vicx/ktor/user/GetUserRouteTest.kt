package no.vicx.ktor.user

import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import no.vicx.ktor.RouteTestBase
import no.vicx.ktor.error.ApiError
import no.vicx.ktor.user.UserTestConstants.API_USER
import no.vicx.ktor.user.vm.UserVm
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.SecurityTestUtils.tokenStringInTest

class GetUserRouteTest :
    RouteTestBase({
        Given("a mocked environment for testing") {
            When("requesting /api/user without authentication") {
                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.get(API_USER)
                    }

                Then("the response status should be Unauthorized") {
                    response.status shouldBe HttpStatusCode.Unauthorized

                    coVerify { mockUserRepository wasNot called }
                }
            }

            When("requesting /api/user with valid authentication when the user exists") {
                coEvery { mockUserRepository.findByUsername(USERNAME_IN_TEST) } returns userModelInTest

                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.get(API_USER) { bearerAuth(tokenStringInTest) }
                    }

                Then("the response status should be OK") {
                    response.status shouldBe HttpStatusCode.OK

                    coVerify(exactly = 1) { mockUserRepository.findByUsername(USERNAME_IN_TEST) }
                }

                And("the response body should deserialize into a UserVm object with expected properties") {
                    response.body<UserVm>() shouldBe userModelInTest.toViewModel()
                }
            }

            When("requesting /api/user with valid authentication when the user does not exist") {
                coEvery { mockUserRepository.findByUsername(any()) } returns null

                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.get(API_USER) { bearerAuth(tokenStringInTest) }
                    }

                Then("the response status should be NotFound") {
                    response.status shouldBe HttpStatusCode.NotFound

                    coVerify(exactly = 1) { mockUserRepository.findByUsername(USERNAME_IN_TEST) }
                }

                And("the response body should contain an ApiError with user-not-found error") {
                    response.body<ApiError>().message shouldBe "User $USERNAME_IN_TEST not found"
                }
            }
        }
    })
