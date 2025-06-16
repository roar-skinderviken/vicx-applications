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
import no.vicx.ktor.user.UserTestConstants.API_USER
import no.vicx.ktor.user.vm.UserPatchVm
import no.vicx.ktor.util.MiscTestUtils.assertValidationErrors
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
                performPatchRequest(httpClient, userPatchVm, false)
            }

            Then("expect Unauthorized") {
                response.status shouldBe HttpStatusCode.Unauthorized

                coVerify { routeTestContext.userRepository wasNot called }
            }
        }

        When("calling PATCH /api/user with authentication and user in db") {
            coEvery { routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST) } returns userModelInTest
            coEvery { routeTestContext.userRepository.updateUser(any(), any(), any(), any()) } just Runs

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                performPatchRequest(httpClient, userPatchVm)
            }

            Then("expect OK") {
                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe "User updated successfully."
                coVerify(exactly = 1) {
                    routeTestContext.userRepository.findByUsername(userModelInTest.username)
                    routeTestContext.userRepository.updateUser(
                        userModelInTest.id,
                        userModelInTest.name,
                        userModelInTest.email,
                        isNull()
                    )
                }
            }
        }

        When("calling PATCH /api/user with authentication and no user in db") {
            coEvery { routeTestContext.userRepository.findByUsername(any()) } returns null

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                performPatchRequest(httpClient, userPatchVm)
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
                "Empty name and email", UserPatchVm("", ""),
                mapOf(
                    "name" to "Name and email cannot both be blank",
                    "email" to "Email and name cannot both be blank"
                )
            ),
            Row3(
                "Name with leading blank", UserPatchVm(" user-name", ""),
                mapOf("name" to "Name cannot have leading or trailing blanks")
            ),
            Row3(
                "Name with trailing blank", UserPatchVm("user-name ", ""),
                mapOf("name" to "Name cannot have leading or trailing blanks")
            ),
            Row3(
                "Name with both leading and trailing blank", UserPatchVm(" user-name ", ""),
                mapOf("name" to "Name cannot have leading or trailing blanks")
            ),

            Row3(
                "Name too short", UserPatchVm("a".repeat(3), "user@example.com"),
                mapOf("name" to "Name must be between 4 and 255 characters")
            ),
            Row3(
                "Name too long", UserPatchVm("a".repeat(256), "user@example.com"),
                mapOf("name" to "Name must be between 4 and 255 characters")
            ),

            Row3(
                "Blank email address, valid name", UserPatchVm("user1", " ".repeat(10)),
                mapOf("email" to "Email format is invalid")
            ),
            Row3(
                "Invalid email address", UserPatchVm("", "a".repeat(10)),
                mapOf("email" to "Email format is invalid")
            ),
            Row3(
                "Email address with leading blank", UserPatchVm("", " user@example.com"),
                mapOf("email" to "Email format is invalid")
            ),
            Row3(
                "Email address with trailing blank", UserPatchVm("", "user@example.com "),
                mapOf("email" to "Email format is invalid")
            )
        ) { description, patchVm, expectedValidationErrors ->
            When("calling PATCH /api/user: $description") {
                coEvery { routeTestContext.userRepository.findByUsername(any()) } returns null

                val response = routeTestContext.runInTestApplicationContext { httpClient ->
                    performPatchRequest(httpClient, patchVm)
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
            body: UserPatchVm,
            addBearerAuth: Boolean = true,
        ): HttpResponse = httpClient.patch(API_USER) {
            if (addBearerAuth) bearerAuth(tokenStringInTest)
            contentType(ContentType.Application.Json)
            setBody<UserPatchVm>(body)
        }

        val userPatchVm = UserPatchVm(
            userModelInTest.name,
            userModelInTest.email
        )

        fun localAssertValidationErrors(
            apiError: ApiError,
            expectedValidationErrors: Map<String, String>,
        ) = assertValidationErrors(apiError, API_USER, expectedValidationErrors)
    }
}