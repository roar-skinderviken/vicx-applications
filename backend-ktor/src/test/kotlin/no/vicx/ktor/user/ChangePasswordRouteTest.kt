package no.vicx.ktor.user

import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.Runs
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import no.vicx.ktor.RouteTestBase
import no.vicx.ktor.error.ApiError
import no.vicx.ktor.user.UserTestConstants.API_USER_PASSWORD
import no.vicx.ktor.user.vm.ChangePasswordVm
import no.vicx.ktor.util.MiscTestUtils.VALID_PLAINTEXT_PASSWORD
import no.vicx.ktor.util.MiscTestUtils.assertValidationErrors
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.SecurityTestUtils.tokenStringInTest

class ChangePasswordRouteTest :
    RouteTestBase({
        Given("a mocked environment for testing") {
            When("updating the password without authentication") {
                val response =
                    withTestApplicationContext { httpClient ->
                        performPatchRequest(httpClient, changePasswordVm, false)
                    }

                Then("the response status should be Unauthorized") {
                    response.status shouldBe HttpStatusCode.Unauthorized

                    coVerify { mockUserRepository wasNot called }
                }
            }

            When("updating the password with valid authentication") {
                coEvery { mockUserRepository.findByUsername(USERNAME_IN_TEST) } returns userModelInTest
                coEvery { mockUserRepository.updateUser(any(), any(), any(), any()) } just Runs

                val response =
                    withTestApplicationContext { httpClient ->
                        performPatchRequest(httpClient, changePasswordVm)
                    }

                Then("the response status should be OK") {
                    response.status shouldBe HttpStatusCode.OK

                    coVerify(exactly = 1) {
                        mockUserRepository.findByUsername(userModelInTest.username)
                        mockUserRepository.updateUser(
                            userModelInTest.id,
                            isNull(),
                            isNull(),
                            isNull(inverse = true),
                        )
                    }
                }

                And("the response body should contain an updated-user message") {
                    response.bodyAsText() shouldBe "Your password has been successfully updated."
                }
            }

            When("providing an incorrect current password") {
                coEvery { mockUserRepository.findByUsername(USERNAME_IN_TEST) } returns userModelInTest

                val response =
                    withTestApplicationContext { httpClient ->
                        performPatchRequest(
                            httpClient,
                            ChangePasswordVm(
                                "~invalid-existing-password~",
                                "$VALID_PLAINTEXT_PASSWORD-changed",
                            ),
                        )
                    }

                Then("the response status should be BadRequest") {
                    response.status shouldBe HttpStatusCode.BadRequest

                    coVerify(exactly = 1) { mockUserRepository.findByUsername(userModelInTest.username) }
                    coVerify(exactly = 0) {
                        mockUserRepository.updateUser(any(), any(), any(), any())
                    }
                }

                And("the response body should contain an ApiError with incorrect password error") {
                    localAssertValidationErrors(
                        response.body<ApiError>(),
                        mapOf("currentPassword" to "currentPassword is incorrect"),
                    )
                }
            }

            When("updating the password for a non-existent user") {
                coEvery { mockUserRepository.findByUsername(any()) } returns null

                val response =
                    withTestApplicationContext { httpClient ->
                        performPatchRequest(httpClient, changePasswordVm)
                    }

                Then("the response status should be NotFound") {
                    response.status shouldBe HttpStatusCode.NotFound

                    coVerify(exactly = 1) { mockUserRepository.findByUsername(any()) }
                    coVerify(exactly = 0) { mockUserRepository.updateUser(any(), any(), any(), any()) }
                }

                And("the response body should contain an ApiError with user-not-found error") {
                    response.body<ApiError>().message shouldBe "User $USERNAME_IN_TEST not found"
                }
            }

            forAll(
                Row3(
                    "Empty currentPassword and new password",
                    ChangePasswordVm("", ""),
                    mapOf(
                        "currentPassword" to "currentPassword cannot be blank",
                        "password" to "Password cannot be blank",
                    ),
                ),
                Row3(
                    "currentPassword too short",
                    ChangePasswordVm("P4s", VALID_PLAINTEXT_PASSWORD),
                    mapOf("currentPassword" to "currentPassword must be between 4 and 255 characters"),
                ),
                Row3(
                    "currentPassword too long",
                    ChangePasswordVm("a".repeat(256), VALID_PLAINTEXT_PASSWORD),
                    mapOf("currentPassword" to "currentPassword must be between 4 and 255 characters"),
                ),
                Row3(
                    "new password too short",
                    ChangePasswordVm(VALID_PLAINTEXT_PASSWORD, "a".repeat(7)),
                    mapOf("password" to "Password must be between 8 and 255 characters"),
                ),
                Row3(
                    "new password too long",
                    ChangePasswordVm(VALID_PLAINTEXT_PASSWORD, "a".repeat(256)),
                    mapOf("password" to "Password must be between 8 and 255 characters"),
                ),
                Row3(
                    "invalid new password",
                    ChangePasswordVm(VALID_PLAINTEXT_PASSWORD, "a".repeat(8)),
                    mapOf("password" to "Password must contain at least one lowercase letter, one uppercase letter, and one digit"),
                ),
            ) { description, changePasswordVm, expectedValidationErrors ->
                When("providing invalid input: $description") {
                    coEvery { mockUserRepository.findByUsername(any()) } returns null

                    val response =
                        withTestApplicationContext { httpClient ->
                            performPatchRequest(httpClient, changePasswordVm)
                        }

                    Then("the response status should be BadRequest") {
                        response.status shouldBe HttpStatusCode.BadRequest

                        coVerify { mockUserRepository wasNot called }
                    }

                    And("the response body should contain an ApiError with the expected validation errors") {
                        localAssertValidationErrors(
                            apiError = response.body<ApiError>(),
                            expectedValidationErrors = expectedValidationErrors,
                        )
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
        ): HttpResponse =
            httpClient.patch(API_USER_PASSWORD) {
                if (addBearerAuth) bearerAuth(tokenStringInTest)
                contentType(ContentType.Application.Json)
                setBody<ChangePasswordVm>(body)
            }

        val changePasswordVm =
            ChangePasswordVm(
                VALID_PLAINTEXT_PASSWORD,
                "$VALID_PLAINTEXT_PASSWORD-changed",
            )

        fun localAssertValidationErrors(
            apiError: ApiError,
            expectedValidationErrors: Map<String, String>,
        ) = assertValidationErrors(apiError, API_USER_PASSWORD, expectedValidationErrors)
    }
}
