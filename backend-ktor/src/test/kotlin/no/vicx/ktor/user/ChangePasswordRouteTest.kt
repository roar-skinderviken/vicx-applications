package no.vicx.ktor.user

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.*
import no.vicx.ktor.error.ApiError
import no.vicx.ktor.user.UserTestConstants.API_USER_PASSWORD
import no.vicx.ktor.user.vm.ChangePasswordVm
import no.vicx.ktor.util.MiscTestUtils.VALID_PLAINTEXT_PASSWORD
import no.vicx.ktor.util.MiscTestUtils.assertValidationErrors
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.RouteTestContext
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.SecurityTestUtils.tokenStringInTest

class ChangePasswordRouteTest : BehaviorSpec({
    coroutineTestScope = true
    val routeTestContext = RouteTestContext()

    Given("mocked environment") {
        beforeContainer {
            clearAllMocks()
        }

        When("calling PATCH /api/user/password without authentication") {
            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                performPatchRequest(httpClient, changePasswordVm, false)
            }

            Then("expect Unauthorized") {
                response.status shouldBe HttpStatusCode.Unauthorized

                coVerify { routeTestContext.userRepository wasNot called }
            }
        }

        When("calling PATCH /api/user/password with authentication and user in db") {
            coEvery { routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST) } returns userModelInTest
            coEvery { routeTestContext.userRepository.updateUser(any(), any(), any(), any()) } just Runs

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                performPatchRequest(httpClient, changePasswordVm)
            }

            Then("expect OK") {
                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe "Your password has been successfully updated."
                coVerify(exactly = 1) {
                    routeTestContext.userRepository.findByUsername(userModelInTest.username)
                    routeTestContext.userRepository.updateUser(
                        userModelInTest.id,
                        isNull(),
                        isNull(),
                        isNull(inverse = true)
                    )
                }
            }
        }

        When("calling PATCH /api/user/password with wrong existing password") {
            coEvery { routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST) } returns userModelInTest

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                performPatchRequest(
                    httpClient, ChangePasswordVm(
                        "~invalid-existing-password~",
                        "$VALID_PLAINTEXT_PASSWORD-changed"
                    )
                )
            }

            Then("expect BadRequest") {
                response.status shouldBe HttpStatusCode.BadRequest

                localAssertValidationErrors(
                    response.body<ApiError>(),
                    mapOf("currentPassword" to "currentPassword is incorrect")
                )

                coVerify(exactly = 1) { routeTestContext.userRepository.findByUsername(userModelInTest.username) }
                coVerify(exactly = 0) {
                    routeTestContext.userRepository.updateUser(any(), any(), any(), any())
                }
            }
        }

        When("calling PATCH /api/user/password with no user in db") {
            coEvery { routeTestContext.userRepository.findByUsername(any()) } returns null

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                performPatchRequest(httpClient, changePasswordVm)
            }

            Then("expect NotFound") {
                response.status shouldBe HttpStatusCode.NotFound
                response.body<ApiError>().message shouldBe "User $USERNAME_IN_TEST not found"

                coVerify(exactly = 1) { routeTestContext.userRepository.findByUsername(any()) }
                coVerify(exactly = 0) { routeTestContext.userRepository.updateUser(any(), any(), any(), any()) }
            }
        }

        forAll(
            Row3(
                "Empty currentPassword and new password", ChangePasswordVm("", ""),
                mapOf(
                    "currentPassword" to "currentPassword cannot be blank",
                    "password" to "Password cannot be blank"
                )
            ),
            Row3(
                "currentPassword too short", ChangePasswordVm("P4s", VALID_PLAINTEXT_PASSWORD),
                mapOf("currentPassword" to "currentPassword must be between 4 and 255 characters")
            ),
            Row3(
                "currentPassword too long", ChangePasswordVm("a".repeat(256), VALID_PLAINTEXT_PASSWORD),
                mapOf("currentPassword" to "currentPassword must be between 4 and 255 characters")
            ),

            Row3(
                "new password too short", ChangePasswordVm(VALID_PLAINTEXT_PASSWORD, "a".repeat(7)),
                mapOf("password" to "Password must be between 8 and 255 characters")
            ),
            Row3(
                "new password too long", ChangePasswordVm(VALID_PLAINTEXT_PASSWORD, "a".repeat(256)),
                mapOf("password" to "Password must be between 8 and 255 characters")
            ),
            Row3(
                "invalid new password", ChangePasswordVm(VALID_PLAINTEXT_PASSWORD, "a".repeat(8)),
                mapOf("password" to "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
            ),
        ) { description, changePasswordVm, expectedValidationErrors ->
            When("calling PATCH /api/user/password: $description") {
                coEvery { routeTestContext.userRepository.findByUsername(any()) } returns null

                val response = routeTestContext.runInTestApplicationContext { httpClient ->
                    performPatchRequest(httpClient, changePasswordVm)
                }

                Then("expect BadRequest") {
                    response.status shouldBe HttpStatusCode.BadRequest
                    localAssertValidationErrors(
                        apiError = response.body<ApiError>(),
                        expectedValidationErrors = expectedValidationErrors
                    )

                    coVerify { routeTestContext.userRepository wasNot called }
                }
            }
        }
    }
}) {
    companion object {

        suspend fun performPatchRequest(
            httpClient: HttpClient,
            body: ChangePasswordVm,
            addBearerAuth: Boolean = true,
        ): HttpResponse = httpClient.patch(API_USER_PASSWORD) {
            if (addBearerAuth) bearerAuth(tokenStringInTest)
            contentType(ContentType.Application.Json)
            setBody<ChangePasswordVm>(body)
        }

        val changePasswordVm = ChangePasswordVm(
            VALID_PLAINTEXT_PASSWORD,
            "$VALID_PLAINTEXT_PASSWORD-changed"
        )

        fun localAssertValidationErrors(
            apiError: ApiError,
            expectedValidationErrors: Map<String, String>,
        ) = assertValidationErrors(apiError, API_USER_PASSWORD, expectedValidationErrors)
    }
}