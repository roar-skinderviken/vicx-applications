package no.vicx.ktor.user

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.mockk.*
import no.vicx.ktor.error.ApiError
import no.vicx.ktor.user.UserTestConstants.API_USER_IMAGE
import no.vicx.ktor.util.MiscTestUtils.GIF_CONTENT_TYPE
import no.vicx.ktor.util.MiscTestUtils.GIF_RESOURCE_NAME
import no.vicx.ktor.util.MiscTestUtils.JPEG_CONTENT_TYPE
import no.vicx.ktor.util.MiscTestUtils.JPEG_RESOURCE_NAME
import no.vicx.ktor.util.MiscTestUtils.PNG_CONTENT_TYPE
import no.vicx.ktor.util.MiscTestUtils.PNG_RESOURCE_NAME
import no.vicx.ktor.util.MiscTestUtils.TOO_LARGE_RESOURCE_NAME
import no.vicx.ktor.util.MiscTestUtils.getResourceAsByteArray
import no.vicx.ktor.util.MiscTestUtils.userImageModelInTest
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.RouteTestContext
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.SecurityTestUtils.tokenStringInTest

class UserImageRouteTest : BehaviorSpec({
    coroutineTestScope = true
    val routeTestContext = RouteTestContext()

    Given("mocked environment") {
        beforeContainer {
            clearAllMocks()
        }

        When("calling POST /user/image without authentication") {
            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.post(API_USER_IMAGE) {
                    contentType(ContentType.MultiPart.FormData)
                    setBody(createMultiPartFormDataContent("image/png", "test-png.png"))
                }
            }

            Then("expect unauthorized") {
                response.status shouldBe HttpStatusCode.Unauthorized
            }
        }

        forAll(
            Row3(
                GIF_CONTENT_TYPE, GIF_RESOURCE_NAME,
                "Image file type: Only PNG and JPG files are allowed"
            ),
            Row3(
                PNG_CONTENT_TYPE, TOO_LARGE_RESOURCE_NAME,
                "Image file size exceeds the maximum allowed size of 51200 bytes"
            ),
        ) { contentType, resourceName, expectedError ->

            When("calling POST /user/image with $contentType") {
                val response = routeTestContext.runInTestApplicationContext { httpClient ->
                    httpClient.post(API_USER_IMAGE) {
                        bearerAuth(tokenStringInTest)
                        contentType(ContentType.MultiPart.FormData)
                        setBody(createMultiPartFormDataContent(contentType, resourceName))
                    }
                }

                Then("expect BadRequest") {
                    response.status shouldBe HttpStatusCode.BadRequest

                    val apiError = response.body<ApiError>()

                    apiError.validationErrors shouldBe mapOf("image" to expectedError)
                }
            }
        }

        forAll(
            row(JPEG_CONTENT_TYPE, JPEG_RESOURCE_NAME, false),
            row(PNG_CONTENT_TYPE, PNG_RESOURCE_NAME, false),
            row(PNG_CONTENT_TYPE, PNG_RESOURCE_NAME, true),
        ) { contentType, resourceName, hasExistingImage ->

            When("calling POST /user/image with valid $contentType, $resourceName, $hasExistingImage") {
                val expectedUserModel =
                    if (hasExistingImage) userModelInTest else userModelInTest.copy(userImage = null)

                coEvery { routeTestContext.userRepository.findByUsername(any()) } returns expectedUserModel
                coEvery { routeTestContext.userImageRepository.saveUserImage(any()) } just Runs
                coEvery { routeTestContext.userImageRepository.updateUserImage(any()) } just Runs

                val response = routeTestContext.runInTestApplicationContext { httpClient ->
                    httpClient.post(API_USER_IMAGE) {
                        bearerAuth(tokenStringInTest)
                        contentType(ContentType.MultiPart.FormData)
                        setBody(createMultiPartFormDataContent(contentType, resourceName))
                    }
                }

                Then("expect Created") {
                    response.status shouldBe HttpStatusCode.Created

                    coVerify(exactly = 1) {
                        if (hasExistingImage) routeTestContext.userImageRepository.updateUserImage(any())
                        else routeTestContext.userImageRepository.saveUserImage(any())
                    }
                }
            }
        }

        When("calling GET /user/image without authentication") {
            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.get(API_USER_IMAGE)
            }

            Then("expect Unauthorized") {
                response.status shouldBe HttpStatusCode.Unauthorized
            }
        }

        When("calling GET /user/image for user without image") {
            coEvery {
                routeTestContext.userRepository.findByUsername(any())
            } returns userModelInTest.copy(userImage = null)

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.get(API_USER_IMAGE) { bearerAuth(tokenStringInTest) }
            }

            Then("expect NotFound") {
                response.status shouldBe HttpStatusCode.NotFound

                coVerify(exactly = 1) {
                    routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST)
                }
            }
        }

        When("calling GET /user/image for user with image") {
            coEvery { routeTestContext.userRepository.findByUsername(any()) } returns userModelInTest

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.get(API_USER_IMAGE) { bearerAuth(tokenStringInTest) }
            }

            Then("expect OK") {
                response.status shouldBe HttpStatusCode.OK

                response.headers[HttpHeaders.ContentType] shouldContain userImageModelInTest.contentType
                response.body<ByteArray>().contentEquals(userImageModelInTest.imageData)

                coVerify(exactly = 1) {
                    routeTestContext.userRepository.findByUsername(USERNAME_IN_TEST)
                }
            }
        }

        When("calling DELETE /user/image for non-existing user") {
            coEvery { routeTestContext.userRepository.findIdByUsername(any()) } returns null

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.delete(API_USER_IMAGE) { bearerAuth(tokenStringInTest) }
            }

            Then("expect NotFound") {
                response.status shouldBe HttpStatusCode.NotFound
                coVerify { routeTestContext.userImageRepository wasNot called }
            }
        }

        When("calling DELETE /user/image for existing user") {
            coEvery { routeTestContext.userRepository.findIdByUsername(any()) } returns userModelInTest.id
            coEvery { routeTestContext.userImageRepository.deleteById(any()) } just Runs

            val response = routeTestContext.runInTestApplicationContext { httpClient ->
                httpClient.delete(API_USER_IMAGE) { bearerAuth(tokenStringInTest) }
            }

            Then("expect NoContent") {
                response.status shouldBe HttpStatusCode.NoContent

                coVerify(exactly = 1) {
                    routeTestContext.userImageRepository.deleteById(userModelInTest.id)
                }
            }
        }
    }
}) {
    companion object {
        fun createMultiPartFormDataContent(
            contentType: String,
            resourceName: String
        ) = MultiPartFormDataContent(
            formData {
                append(
                    "image",
                    getResourceAsByteArray("/$resourceName"),
                    Headers.build {
                        append(HttpHeaders.ContentType, ContentType.parse(contentType))
                        append(HttpHeaders.ContentDisposition, "filename=\"$resourceName\"")
                    }
                )
            }
        )
    }
}