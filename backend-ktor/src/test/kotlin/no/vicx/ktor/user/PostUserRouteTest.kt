package no.vicx.ktor.user

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
import no.vicx.ktor.error.ApiError
import no.vicx.ktor.plugins.VALIDATION_ERROR
import no.vicx.ktor.user.UserTestConstants.API_USER
import no.vicx.ktor.user.vm.CreateUserVm
import no.vicx.ktor.util.MiscTestUtils.GIF_CONTENT_TYPE
import no.vicx.ktor.util.MiscTestUtils.GIF_RESOURCE_NAME
import no.vicx.ktor.util.MiscTestUtils.PNG_CONTENT_TYPE
import no.vicx.ktor.util.MiscTestUtils.PNG_RESOURCE_NAME
import no.vicx.ktor.util.MiscTestUtils.TOO_LARGE_RESOURCE_NAME
import no.vicx.ktor.util.MiscTestUtils.VALID_BCRYPT_PASSWORD
import no.vicx.ktor.util.MiscTestUtils.VALID_PLAINTEXT_PASSWORD
import no.vicx.ktor.util.MiscTestUtils.getResourceAsByteArray
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.RouteTestContext

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
                routeTestContext.userRepository.findIdByUsername(any())
            } returns userModelInTest.id

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
            coEvery { routeTestContext.userRepository.findIdByUsername(any()) } returns null
            coEvery { routeTestContext.userRepository.createUser(any()) } returns validCreateUserVm.toDbModel(
                VALID_BCRYPT_PASSWORD
            )

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.post(API_USER) {
                    contentType(ContentType.MultiPart.FormData)
                    setBody(
                        createMultiPartFormDataContent(
                            validCreateUserVm,
                            PNG_CONTENT_TYPE to PNG_RESOURCE_NAME
                        )
                    )
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
                null, "username", "Username cannot be blank"
            ),
            row(
                CreateUserVm(" ".repeat(4), VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                null, "username", "Username cannot be blank"
            ),
            row(
                CreateUserVm("a", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                null, "username", "Username must be between 4 and 255 characters"
            ),
            row(
                CreateUserVm("a".repeat(256), VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                null, "username", "Username must be between 4 and 255 characters"
            ),
            row(
                CreateUserVm("John Doe", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                null, "username", "Username can only contain letters, numbers, hyphens, and underscores"
            ),

            row(
                CreateUserVm("user1", "", "The User", "user@example.com", "mock-token"),
                null, "password", "Password cannot be blank"
            ),
            row(
                CreateUserVm("user1", " ".repeat(4), "The User", "user@example.com", "mock-token"),
                null, "password", "Password cannot be blank"
            ),
            row(
                CreateUserVm("user1", "Aa1Aa1", "The User", "user@example.com", "mock-token"),
                null, "password", "Password must be between 8 and 255 characters"
            ),
            row(
                CreateUserVm("user1", "Aa1".repeat(90), "The User", "user@example.com", "mock-token"),
                null, "password", "Password must be between 8 and 255 characters"
            ),
            row(
                CreateUserVm("user1", "a".repeat(8), "The User", "user@example.com", "mock-token"),
                null,
                "password",
                "Password must contain at least one lowercase letter, one uppercase letter, and one digit"
            ),

            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "", "user@example.com", "mock-token"),
                null, "name", "Name cannot be blank"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, " ".repeat(4), "user@example.com", "mock-token"),
                null, "name", "Name cannot be blank"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "a".repeat(3), "user@example.com", "mock-token"),
                null, "name", "Name must be between 4 and 255 characters"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "a".repeat(256), "user@example.com", "mock-token"),
                null, "name", "Name must be between 4 and 255 characters"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, " The User", "user@example.com", "mock-token"),
                null, "name", "Name cannot have leading or trailing blanks"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User ", "user@example.com", "mock-token"),
                null, "name", "Name cannot have leading or trailing blanks"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, " The User ", "user@example.com", "mock-token"),
                null, "name", "Name cannot have leading or trailing blanks"
            ),

            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "", "mock-token"),
                null, "email", "Email cannot be blank"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "a", "mock-token"),
                null, "email", "Email format is invalid"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", " john.doe@example.com", "mock-token"),
                null, "email", "Email format is invalid"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "john.doe@example.com ", "mock-token"),
                null, "email", "Email format is invalid"
            ),

            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", ""),
                null, "recaptchaToken", "recaptchaToken cannot be blank"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", " ".repeat(4)),
                null, "recaptchaToken", "recaptchaToken cannot be blank"
            ),

            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                GIF_CONTENT_TYPE to GIF_RESOURCE_NAME, "image", "Image file type: Only PNG and JPG files are allowed"
            ),
            row(
                CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                PNG_CONTENT_TYPE to TOO_LARGE_RESOURCE_NAME,
                "image",
                "Image file size exceeds the maximum allowed size of 51200 bytes"
            )
        ) { createUserVm, imageInfo, field, expectedValidationError ->

            When("calling POST /api/user with invalid $field, $createUserVm, $imageInfo") {
                val response = routeTestContext.runInTestApplicationContext { httpClient ->
                    httpClient.post(API_USER) {
                        contentType(ContentType.MultiPart.FormData)
                        setBody(createMultiPartFormDataContent(createUserVm, imageInfo))
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

        fun createMultiPartFormDataContent(
            createUserVm: CreateUserVm,
            userImageInfo: Pair<String, String>? = null
        ) = MultiPartFormDataContent(
            formData {
                if (createUserVm.username.isNotEmpty()) append("username", createUserVm.username)
                if (createUserVm.name.isNotEmpty()) append("name", createUserVm.name)
                if (createUserVm.email.isNotEmpty()) append("email", createUserVm.email)
                if (createUserVm.password.isNotEmpty()) append("password", createUserVm.password)
                if (createUserVm.recaptchaToken.isNotEmpty()) append("recaptchaToken", createUserVm.recaptchaToken)

                if (userImageInfo != null) {
                    val (contentType, resourceName) = userImageInfo

                    append(
                        "image",
                        getResourceAsByteArray("/$resourceName"),
                        Headers.build {
                            append(HttpHeaders.ContentType, ContentType.parse(contentType))
                            append(HttpHeaders.ContentDisposition, "filename=\"$resourceName\"")
                        }
                    )
                }
            }
        )
    }
}