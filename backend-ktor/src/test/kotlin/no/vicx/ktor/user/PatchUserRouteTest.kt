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
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import no.vicx.ktor.RouteTestBase
import no.vicx.ktor.db.repository.UserRepository
import no.vicx.ktor.error.ApiError
import no.vicx.ktor.user.UserTestConstants.API_USER
import no.vicx.ktor.user.vm.UserPatchVm
import no.vicx.ktor.util.MiscTestUtils.assertValidationErrors
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.SecurityTestUtils.tokenStringInTest
import org.koin.test.inject

class PatchUserRouteTest :
    RouteTestBase({
        Given("a mocked environment for testing") {
            val userRepository by inject<UserRepository>()

            beforeContainer {
                clearAllMocks()
            }

            When("patching /api/user without authentication") {
                val response =
                    withTestApplicationContext { httpClient ->
                        performPatchRequest(httpClient, userPatchVm, false)
                    }

                Then("the response status should be Unauthorized") {
                    response.status shouldBe HttpStatusCode.Unauthorized

                    coVerify { userRepository wasNot called }
                }
            }

            When("patching /api/user with authentication when user exists") {
                coEvery { userRepository.findByUsername(USERNAME_IN_TEST) } returns userModelInTest
                coEvery { userRepository.updateUser(any(), any(), any(), any()) } just Runs

                val response =
                    withTestApplicationContext { httpClient ->
                        performPatchRequest(httpClient, userPatchVm)
                    }

                Then("the response status should be OK") {
                    response.status shouldBe HttpStatusCode.OK

                    coVerify(exactly = 1) {
                        userRepository.findByUsername(userModelInTest.username)
                        userRepository.updateUser(
                            userModelInTest.id,
                            userModelInTest.name,
                            userModelInTest.email,
                            isNull(),
                        )
                    }
                }

                And("the response body should contain a user-updated message") {
                    response.bodyAsText() shouldBe "User updated successfully."
                }
            }

            When("patching /api/user with authentication when user does not exist") {
                coEvery { userRepository.findByUsername(any()) } returns null

                val response =
                    withTestApplicationContext { httpClient ->
                        performPatchRequest(httpClient, userPatchVm)
                    }

                Then("the response status should be NotFound") {
                    response.status shouldBe HttpStatusCode.NotFound

                    coVerify(exactly = 1) { userRepository.findByUsername(any()) }
                    coVerify(exactly = 0) { userRepository.updateUser(any(), any(), any(), any()) }
                }

                And("the response body should contain an ApiError with user-not-found error") {
                    response.body<ApiError>().message shouldBe "User $USERNAME_IN_TEST not found"
                }
            }

            forAll(
                Row3(
                    "Empty name and email",
                    UserPatchVm("", ""),
                    mapOf(
                        "name" to "Name and email cannot both be blank",
                        "email" to "Email and name cannot both be blank",
                    ),
                ),
                Row3(
                    "Name with leading blank",
                    UserPatchVm(" user-name", ""),
                    mapOf("name" to "Name cannot have leading or trailing blanks"),
                ),
                Row3(
                    "Name with trailing blank",
                    UserPatchVm("user-name ", ""),
                    mapOf("name" to "Name cannot have leading or trailing blanks"),
                ),
                Row3(
                    "Name with both leading and trailing blank",
                    UserPatchVm(" user-name ", ""),
                    mapOf("name" to "Name cannot have leading or trailing blanks"),
                ),
                Row3(
                    "Name too short",
                    UserPatchVm("a".repeat(3), "user@example.com"),
                    mapOf("name" to "Name must be between 4 and 255 characters"),
                ),
                Row3(
                    "Name too long",
                    UserPatchVm("a".repeat(256), "user@example.com"),
                    mapOf("name" to "Name must be between 4 and 255 characters"),
                ),
                Row3(
                    "Blank email address, valid name",
                    UserPatchVm("user1", " ".repeat(10)),
                    mapOf("email" to "Email format is invalid"),
                ),
                Row3(
                    "Invalid email address",
                    UserPatchVm("", "a".repeat(10)),
                    mapOf("email" to "Email format is invalid"),
                ),
                Row3(
                    "Email address with leading blank",
                    UserPatchVm("", " user@example.com"),
                    mapOf("email" to "Email format is invalid"),
                ),
                Row3(
                    "Email address with trailing blank",
                    UserPatchVm("", "user@example.com "),
                    mapOf("email" to "Email format is invalid"),
                ),
            ) { description, patchVm, expectedValidationErrors ->
                When("patching /api/user with invalid input: $description") {
                    coEvery { userRepository.findByUsername(any()) } returns null

                    val response =
                        withTestApplicationContext { httpClient ->
                            performPatchRequest(httpClient, patchVm)
                        }

                    Then("the response status should be BadRequest") {
                        response.status shouldBe HttpStatusCode.BadRequest

                        coVerify { userRepository wasNot called }
                    }

                    And("the response body should contain an ApiError with expected validation error(s)") {
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
            body: UserPatchVm,
            addBearerAuth: Boolean = true,
        ): HttpResponse =
            httpClient.patch(API_USER) {
                if (addBearerAuth) bearerAuth(tokenStringInTest)
                contentType(ContentType.Application.Json)
                setBody<UserPatchVm>(body)
            }

        val userPatchVm =
            UserPatchVm(
                userModelInTest.name,
                userModelInTest.email,
            )

        fun localAssertValidationErrors(
            apiError: ApiError,
            expectedValidationErrors: Map<String, String>,
        ) = assertValidationErrors(apiError, API_USER, expectedValidationErrors)
    }
}
