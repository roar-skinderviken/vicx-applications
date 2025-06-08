package no.vicx.user

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import no.vicx.error.ApiError
import no.vicx.plugins.VALIDATION_ERROR
import no.vicx.user.UserTestConstants.API_USER
import no.vicx.user.vm.CreateUserVm
import no.vicx.util.RouteTestContext
import no.vicx.util.TestConstants.VALID_BCRYPT_PASSWORD
import no.vicx.util.TestConstants.VALID_PLAINTEXT_PASSWORD
import no.vicx.util.TestConstants.userModelInTest

class PostUserRouteTest : BehaviorSpec({
    coroutineTestScope = true
    val routeTestContext = RouteTestContext()

    Given("mocked environment") {
        beforeContainer {
            clearAllMocks()
        }

        When("calling POST /api/user with invalid reCAPTCHA token") {
            coEvery { routeTestContext.recaptchaClient.verifyToken(any()) } returns false

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.post(API_USER) {
                    contentType(ContentType.MultiPart.FormData)
                    setBody(createMultiPartFormDataContent(validCreateUserVm))
                }
            }

            Then("expect BadRequest and validation error") {
                response.status shouldBe HttpStatusCode.BadRequest
                assertValidationError(
                    response.body<ApiError>(),
                    "recaptchaToken",
                    "recaptchaToken is invalid. Please wait to token expires and try again"
                )

                coVerify(exactly = 0) { routeTestContext.userRepository.createUser(any()) }
            }
        }

        When("calling POST /api/user with duplicate username") {
            coEvery { routeTestContext.recaptchaClient.verifyToken(any()) } returns true
            coEvery {
                routeTestContext.userRepository.findByUsername(any())
            } returns userModelInTest

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.post(API_USER) {
                    contentType(ContentType.MultiPart.FormData)
                    setBody(createMultiPartFormDataContent(validCreateUserVm))
                }
            }

            Then("expect BadRequest and validation error") {
                response.status shouldBe HttpStatusCode.BadRequest
                assertValidationError(
                    response.body<ApiError>(),
                    "username", "Username is already in use"
                )

                coVerify(exactly = 0) { routeTestContext.userRepository.createUser(any()) }
            }
        }

        When("calling POST /api/user with valid data") {
            coEvery { routeTestContext.recaptchaClient.verifyToken(any()) } returns true
            coEvery { routeTestContext.userRepository.findByUsername(any()) } returns null
            coEvery { routeTestContext.userRepository.createUser(any()) } returns validCreateUserVm.toDbModel(
                VALID_BCRYPT_PASSWORD
            )

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.post(API_USER) {
                    contentType(ContentType.MultiPart.FormData)
                    setBody(createMultiPartFormDataContent(validCreateUserVm))
                }
            }

            Then("expect user created content in response") {
                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe "User created successfully."

                coVerify(exactly = 1) { routeTestContext.userRepository.createUser(any()) }
            }
        }

        When("calling POST /api/user twice with duplicate username") {
            coEvery { routeTestContext.recaptchaClient.verifyToken(any()) } returns true
            coEvery { routeTestContext.userRepository.findByUsername(any()) } returns userModelInTest
            coEvery { routeTestContext.userRepository.createUser(any()) } returns validCreateUserVm.toDbModel(
                VALID_BCRYPT_PASSWORD
            )

            routeTestContext.runInTestApplicationContext { httpClient ->
                repeat(2) {
                    httpClient.post(API_USER) {
                        contentType(ContentType.MultiPart.FormData)
                        setBody(createMultiPartFormDataContent(validCreateUserVm))
                    }
                }
            }

            Then("expect single call to recaptchaClient#verifyToken due to caching") {
                coVerify(exactly = 1) { routeTestContext.recaptchaClient.verifyToken(any()) }
            }
        }

        forAll(
            row(
                CreateUserVm("", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                "username", "Username cannot be blank"
            ),
            row(
                CreateUserVm(" ".repeat(4), VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                "username", "Username cannot be blank"
            ),
            row(
                CreateUserVm("a", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                "username", "Username must be between 4 and 255 characters"
            ),
            row(
                CreateUserVm("a".repeat(256), VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                "username", "Username must be between 4 and 255 characters"
            ),
            row(
                CreateUserVm("John Doe", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                "username", "Username can only contain letters, numbers, hyphens, and underscores"
            ),

            row(
                CreateUserVm("user1", "", "The User", "user@example.com", "mock-token"),
                "password", "Password cannot be blank"
            ),
            row(
                CreateUserVm("user1", " ".repeat(4), "The User", "user@example.com", "mock-token"),
                "password", "Password cannot be blank"
            ),
            row(
                CreateUserVm("user1", "Aa1Aa1", "The User", "user@example.com", "mock-token"),
                "password", "Password must be between 8 and 255 characters"
            ),
            row(
                CreateUserVm("user1", "Aa1".repeat(90), "The User", "user@example.com", "mock-token"),
                "password", "Password must be between 8 and 255 characters"
            ),
            row(
                CreateUserVm("user1", "a".repeat(8), "The User", "user@example.com", "mock-token"),
                "password", "Password must contain at least one lowercase letter, one uppercase letter, and one digit"
            ),

            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "", "user@example.com", "mock-token"),
                "name", "Name cannot be blank"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, " ".repeat(4), "user@example.com", "mock-token"),
                "name", "Name cannot be blank"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "a".repeat(3), "user@example.com", "mock-token"),
                "name", "Name must be between 4 and 255 characters"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "a".repeat(256), "user@example.com", "mock-token"),
                "name", "Name must be between 4 and 255 characters"
            ),

            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "", "mock-token"),
                "email", "Email cannot be blank"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "a", "mock-token"),
                "email", "Email format is invalid"
            ),

            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", ""),
                "recaptchaToken", "recaptchaToken cannot be blank"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", " ".repeat(4)),
                "recaptchaToken", "recaptchaToken cannot be blank"
            )
        ) { createUserVm, field, expectedValidationError ->

            When("calling POST /api/user with invalid $field, $createUserVm") {
                val response = routeTestContext.runInTestApplicationContext { httpClient ->
                    httpClient.post(API_USER) {
                        contentType(ContentType.MultiPart.FormData)
                        setBody(createMultiPartFormDataContent(createUserVm))
                    }
                }

                Then("it should return status BadRequest and the expected body") {
                    response.status shouldBe HttpStatusCode.BadRequest
                    assertValidationError(response.body<ApiError>(), field, expectedValidationError)

                    coVerify(exactly = 0) {
                        routeTestContext.recaptchaClient.verifyToken(any())
                        routeTestContext.userRepository.findByUsername(any())
                        routeTestContext.userRepository.createUser(any())
                    }
                }
            }
        }
    }
}) {
    companion object {
        val validCreateUserVm = CreateUserVm(
            "user1", VALID_PLAINTEXT_PASSWORD,
            "The User", "user@example.com", "~invalid-token~"
        )

        fun assertValidationError(
            apiError: ApiError,
            expectedFieldName: String,
            expectedError: String
        ) {
            assertSoftly(apiError) {
                status shouldBe HttpStatusCode.BadRequest.value
                url shouldBe API_USER
                message shouldBe VALIDATION_ERROR
                validationErrors shouldBe mapOf(expectedFieldName to expectedError)
            }
        }

        fun createMultiPartFormDataContent(createUserVm: CreateUserVm) = MultiPartFormDataContent(
            formData {
                append("username", createUserVm.username)
                append("name", createUserVm.name)
                append("email", createUserVm.email)
                append("password", createUserVm.password)
                append("recaptchaToken", createUserVm.recaptchaToken)

                append(
                    "image",
                    "some-data".toByteArray(),
                    Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Image.PNG)
                        append(HttpHeaders.ContentDisposition, "filename=\"profile-image.png\"")
                    }
                )
            }
        )
    }
}