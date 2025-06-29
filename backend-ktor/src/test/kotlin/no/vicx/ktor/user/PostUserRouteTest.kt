package no.vicx.ktor.user

import io.kotest.assertions.assertSoftly
import io.kotest.data.Row4
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.append
import io.ktor.http.contentType
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import no.vicx.ktor.RouteTestBase
import no.vicx.ktor.db.repository.UserRepository
import no.vicx.ktor.error.ApiError
import no.vicx.ktor.plugins.VALIDATION_ERROR
import no.vicx.ktor.user.UserTestConstants.API_USER
import no.vicx.ktor.user.service.RecaptchaClient
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
import org.koin.core.component.inject
import java.util.UUID

class PostUserRouteTest :
    RouteTestBase({
        Given("a mocked environment for testing") {
            val mockUserRepository by inject<UserRepository>()
            val mockRecaptchaClient by inject<RecaptchaClient>()

            beforeContainer {
                clearAllMocks()

                coEvery { mockRecaptchaClient.verifyToken(any()) } returns true
            }

            When("posting to /api/user with invalid reCAPTCHA token") {
                coEvery { mockRecaptchaClient.verifyToken(any()) } returns false

                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.post(API_USER) {
                            contentType(ContentType.MultiPart.FormData)
                            setBody(createMultiPartFormDataContent(validCreateUserVm()))
                        }
                    }

                Then("the response status should be BadRequest") {
                    response.status shouldBe HttpStatusCode.BadRequest

                    coVerify { mockUserRepository wasNot called }
                }

                And("the response body should contain an ApiError with invalid-reCAPTCHA-token error") {
                    assertValidationError(
                        response.body<ApiError>(),
                        "recaptchaToken",
                        "recaptchaToken is invalid. Please wait to token expires and try again",
                    )
                }
            }

            When("posting to /api/user with a duplicate username") {
                coEvery {
                    mockUserRepository.findIdByUsername(any())
                } returns userModelInTest.id

                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.post(API_USER) {
                            contentType(ContentType.MultiPart.FormData)
                            setBody(createMultiPartFormDataContent(validCreateUserVm()))
                        }
                    }

                Then("the response status should be BadRequest") {
                    response.status shouldBe HttpStatusCode.BadRequest

                    coVerify(exactly = 0) { mockUserRepository.createUser(any()) }
                }

                And("the response body should contain an ApiError with duplicate-user error") {
                    assertValidationError(
                        response.body<ApiError>(),
                        "username",
                        "Username is already in use",
                    )
                }
            }

            When("posting to /api/user with valid user data") {
                coEvery { mockUserRepository.findIdByUsername(any()) } returns null
                coEvery { mockUserRepository.createUser(any()) } returns
                    validCreateUserVm().toDbModel(
                        VALID_BCRYPT_PASSWORD,
                    )

                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.post(API_USER) {
                            contentType(ContentType.MultiPart.FormData)
                            setBody(
                                createMultiPartFormDataContent(
                                    validCreateUserVm(),
                                    PNG_CONTENT_TYPE to PNG_RESOURCE_NAME,
                                ),
                            )
                        }
                    }

                Then("the response status should be OK") {
                    response.status shouldBe HttpStatusCode.OK

                    coVerify(exactly = 1) { mockUserRepository.createUser(any()) }
                }

                And("the response body should contain a user-created message") {
                    response.bodyAsText() shouldBe "User created successfully."
                }
            }

            When("posting to /api/user twice with the same username") {
                coEvery { mockUserRepository.findByUsername(any()) } returns userModelInTest
                coEvery { mockUserRepository.createUser(any()) } returns
                    validCreateUserVm().toDbModel(
                        VALID_BCRYPT_PASSWORD,
                    )

                val createUserVm = validCreateUserVm()

                withTestApplicationContext { httpClient ->
                    repeat(2) {
                        httpClient.post(API_USER) {
                            contentType(ContentType.MultiPart.FormData)
                            setBody(createMultiPartFormDataContent(createUserVm))
                        }
                    }
                }

                Then("recaptchaClient#verifyToken should have been called just once due to caching") {
                    coVerify(exactly = 1) { mockRecaptchaClient.verifyToken(any()) }
                }
            }

            forAll(
                Row4(
                    CreateUserVm("", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                    null,
                    "username",
                    "Username cannot be blank",
                ),
                Row4(
                    CreateUserVm(
                        " ".repeat(4),
                        VALID_PLAINTEXT_PASSWORD,
                        "The User",
                        "user@example.com",
                        "mock-token",
                    ),
                    null,
                    "username",
                    "Username cannot be blank",
                ),
                Row4(
                    CreateUserVm("a", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                    null,
                    "username",
                    "Username must be between 4 and 255 characters",
                ),
                Row4(
                    CreateUserVm(
                        "a".repeat(256),
                        VALID_PLAINTEXT_PASSWORD,
                        "The User",
                        "user@example.com",
                        "mock-token",
                    ),
                    null,
                    "username",
                    "Username must be between 4 and 255 characters",
                ),
                Row4(
                    CreateUserVm(
                        "John Doe",
                        VALID_PLAINTEXT_PASSWORD,
                        "The User",
                        "user@example.com",
                        "mock-token",
                    ),
                    null,
                    "username",
                    "Username can only contain letters, numbers, hyphens, and underscores",
                ),
                Row4(
                    CreateUserVm("user1", "", "The User", "user@example.com", "mock-token"),
                    null,
                    "password",
                    "Password cannot be blank",
                ),
                Row4(
                    CreateUserVm("user1", " ".repeat(4), "The User", "user@example.com", "mock-token"),
                    null,
                    "password",
                    "Password cannot be blank",
                ),
                Row4(
                    CreateUserVm("user1", "Aa1Aa1", "The User", "user@example.com", "mock-token"),
                    null,
                    "password",
                    "Password must be between 8 and 255 characters",
                ),
                Row4(
                    CreateUserVm("user1", "Aa1".repeat(90), "The User", "user@example.com", "mock-token"),
                    null,
                    "password",
                    "Password must be between 8 and 255 characters",
                ),
                Row4(
                    CreateUserVm("user1", "a".repeat(8), "The User", "user@example.com", "mock-token"),
                    null,
                    "password",
                    "Password must contain at least one lowercase letter, one uppercase letter, and one digit",
                ),
                Row4(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "", "user@example.com", "mock-token"),
                    null,
                    "name",
                    "Name cannot be blank",
                ),
                Row4(
                    CreateUserVm(
                        "user1",
                        VALID_PLAINTEXT_PASSWORD,
                        " ".repeat(4),
                        "user@example.com",
                        "mock-token",
                    ),
                    null,
                    "name",
                    "Name cannot be blank",
                ),
                Row4(
                    CreateUserVm(
                        "user1",
                        VALID_PLAINTEXT_PASSWORD,
                        "a".repeat(3),
                        "user@example.com",
                        "mock-token",
                    ),
                    null,
                    "name",
                    "Name must be between 4 and 255 characters",
                ),
                Row4(
                    CreateUserVm(
                        "user1",
                        VALID_PLAINTEXT_PASSWORD,
                        "a".repeat(256),
                        "user@example.com",
                        "mock-token",
                    ),
                    null,
                    "name",
                    "Name must be between 4 and 255 characters",
                ),
                Row4(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, " The User", "user@example.com", "mock-token"),
                    null,
                    "name",
                    "Name cannot have leading or trailing blanks",
                ),
                Row4(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User ", "user@example.com", "mock-token"),
                    null,
                    "name",
                    "Name cannot have leading or trailing blanks",
                ),
                Row4(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, " The User ", "user@example.com", "mock-token"),
                    null,
                    "name",
                    "Name cannot have leading or trailing blanks",
                ),
                Row4(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "", "mock-token"),
                    null,
                    "email",
                    "Email cannot be blank",
                ),
                Row4(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "a", "mock-token"),
                    null,
                    "email",
                    "Email format is invalid",
                ),
                Row4(
                    CreateUserVm(
                        "user1",
                        VALID_PLAINTEXT_PASSWORD,
                        "The User",
                        " john.doe@example.com",
                        "mock-token",
                    ),
                    null,
                    "email",
                    "Email format is invalid",
                ),
                Row4(
                    CreateUserVm(
                        "user1",
                        VALID_PLAINTEXT_PASSWORD,
                        "The User",
                        "john.doe@example.com ",
                        "mock-token",
                    ),
                    null,
                    "email",
                    "Email format is invalid",
                ),
                Row4(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", ""),
                    null,
                    "recaptchaToken",
                    "recaptchaToken cannot be blank",
                ),
                Row4(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", " ".repeat(4)),
                    null,
                    "recaptchaToken",
                    "recaptchaToken cannot be blank",
                ),
                Row4(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                    GIF_CONTENT_TYPE to GIF_RESOURCE_NAME,
                    "image",
                    "Image file type: Only PNG and JPG files are allowed",
                ),
                Row4(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                    PNG_CONTENT_TYPE to TOO_LARGE_RESOURCE_NAME,
                    "image",
                    "Image file size exceeds the maximum allowed size of 51200 bytes",
                ),
            ) { createUserVm, imageInfo, field, expectedValidationError ->

                When("posting to /api/user with invalid $field: $createUserVm, $imageInfo") {
                    val response =
                        withTestApplicationContext { httpClient ->
                            httpClient.post(API_USER) {
                                contentType(ContentType.MultiPart.FormData)
                                setBody(createMultiPartFormDataContent(createUserVm, imageInfo))
                            }
                        }

                    Then("the response status should be BadRequest") {
                        response.status shouldBe HttpStatusCode.BadRequest

                        coVerify {
                            mockRecaptchaClient wasNot called
                            mockUserRepository wasNot called
                        }
                    }

                    And("the response body should contain an ApiError with expected validation errors") {
                        assertValidationError(response.body<ApiError>(), field, expectedValidationError)
                    }
                }
            }
        }
    }) {
    companion object {
        fun validCreateUserVm() =
            CreateUserVm(
                "user1",
                VALID_PLAINTEXT_PASSWORD,
                "The User",
                "user@example.com",
                UUID.randomUUID().toString(),
            )

        fun assertValidationError(
            apiError: ApiError,
            expectedFieldName: String,
            expectedError: String,
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
            userImageInfo: Pair<String, String>? = null,
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
                        },
                    )
                }
            },
        )
    }
}
