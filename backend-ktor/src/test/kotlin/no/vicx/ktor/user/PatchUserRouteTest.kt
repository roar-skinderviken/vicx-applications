package no.vicx.ktor.user

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.*
import no.vicx.ktor.error.ApiError
import no.vicx.ktor.plugins.VALIDATION_ERROR
import no.vicx.ktor.user.UserTestConstants.API_USER
import no.vicx.ktor.user.vm.UserPatchVm
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.RouteTestContext
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.SecurityTestUtils.tokenStringInTest

class PatchUserRouteTest : BehaviorSpec({
    coroutineTestScope = true
    val routeTestContext = RouteTestContext()

    Given("mocked environment") {
        beforeContainer {
            clearAllMocks()
        }

        When("calling PATCH /api/user without authentication") {
            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.patch(API_USER)
            }

            Then("expect Unauthorized") {
                response.status shouldBe HttpStatusCode.Unauthorized

                coVerify(exactly = 0) { routeTestContext.userRepository.updateUser(any(), any(), any()) }
            }
        }

        When("calling PATCH /api/user with authentication and user in db") {
            coEvery { routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST) } returns userModelInTest
            coEvery { routeTestContext.userRepository.updateUser(any(), any(), any()) } just Runs

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.patch(API_USER) {
                    bearerAuth(tokenStringInTest)
                    contentType(ContentType.Application.Json)
                    setBody<UserPatchVm>(
                        UserPatchVm(
                            userModelInTest.name,
                            userModelInTest.email
                        )
                    )
                }
            }

            Then("expect OK") {
                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe "User updated successfully."
                coVerify(exactly = 1) {
                    routeTestContext.userRepository.findByUsername(userModelInTest.username)
                    routeTestContext.userRepository.updateUser(any(), userModelInTest.name, userModelInTest.email)
                }
            }
        }

        When("calling PATCH /api/user with authentication and no user in db") {
            coEvery { routeTestContext.userRepository.findByUsername(any()) } returns null

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.patch(API_USER) {
                    bearerAuth(tokenStringInTest)
                    contentType(ContentType.Application.Json)
                    setBody<UserPatchVm>(
                        UserPatchVm(
                            userModelInTest.name,
                            userModelInTest.email
                        )
                    )
                }
            }

            Then("expect NotFound") {
                response.status shouldBe HttpStatusCode.NotFound
                response.body<ApiError>().message shouldBe "User $USERNAME_IN_TEST not found"

                coVerify(exactly = 1) { routeTestContext.userRepository.findByUsername(any()) }
                coVerify(exactly = 0) { routeTestContext.userRepository.updateUser(any(), any(), any()) }
            }
        }

        forAll(
            row(
                "Empty name and email", UserPatchVm("", ""),
                mapOf(
                    "name" to "Name and email cannot both be blank",
                    "email" to "Email and name cannot both be blank"
                )
            ),
            row(
                "Name too short", UserPatchVm("a".repeat(3), "user@example.com"),
                mapOf("name" to "Name must be between 4 and 255 characters")
            ),
            row(
                "Name too long", UserPatchVm("a".repeat(256), "user@example.com"),
                mapOf("name" to "Name must be between 4 and 255 characters")
            ),

            row(
                "Invalid email address", UserPatchVm("", "a".repeat(10)),
                mapOf("email" to "Email format is invalid")
            )
        ) { description, patchVm, expectedValidationErrors ->
            When("calling PATCH /api/user: $description") {
                coEvery { routeTestContext.userRepository.findByUsername(any()) } returns null

                val response = routeTestContext.runInTestApplicationContext { httpClient ->
                    httpClient.patch(API_USER) {
                        bearerAuth(tokenStringInTest)
                        contentType(ContentType.Application.Json)
                        setBody<UserPatchVm>(patchVm)
                    }
                }

                Then("expect BadRequest") {
                    response.status shouldBe HttpStatusCode.BadRequest
                    assertValidationErrors(
                        apiError = response.body<ApiError>(),
                        expectedValidationErrors = expectedValidationErrors
                    )

                    coVerify(exactly = 0) {
                        routeTestContext.userRepository.findByUsername(any())
                        routeTestContext.userRepository.updateUser(any(), any(), any())
                    }
                }
            }
        }
    }
}) {

    companion object {
        fun assertValidationErrors(
            apiError: ApiError,
            expectedValidationErrors: Map<String, String>,
        ) {
            assertSoftly(apiError) {
                status shouldBe HttpStatusCode.BadRequest.value
                url shouldBe API_USER
                message shouldBe VALIDATION_ERROR
                validationErrors shouldBe expectedValidationErrors
            }
        }
    }
}