package no.vicx.ktor.user

import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.append
import io.ktor.http.contentType
import io.mockk.Runs
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import no.vicx.ktor.RouteTestBase
import no.vicx.ktor.db.repository.UserImageRepository
import no.vicx.ktor.db.repository.UserRepository
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
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.ktor.util.SecurityTestUtils.tokenStringInTest
import org.koin.test.inject

class UserImageRouteTest :
    RouteTestBase({
        Given("a mocked environment for testing") {
            val userRepository by inject<UserRepository>()
            val userImageRepository by inject<UserImageRepository>()

            When("posting to /user/image without authentication") {
                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.post(API_USER_IMAGE) {
                            contentType(ContentType.MultiPart.FormData)
                            setBody(createMultiPartFormDataContent("image/png", "test-png.png"))
                        }
                    }

                Then("the response status should be Unauthorized") {
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }

            forAll(
                Row3(
                    GIF_CONTENT_TYPE,
                    GIF_RESOURCE_NAME,
                    "Image file type: Only PNG and JPG files are allowed",
                ),
                Row3(
                    PNG_CONTENT_TYPE,
                    TOO_LARGE_RESOURCE_NAME,
                    "Image file size exceeds the maximum allowed size of 51200 bytes",
                ),
            ) { contentType, resourceName, expectedError ->

                When("posting to /user/image with invalid image type $contentType") {
                    val response =
                        withTestApplicationContext { httpClient ->
                            httpClient.post(API_USER_IMAGE) {
                                bearerAuth(tokenStringInTest)
                                contentType(ContentType.MultiPart.FormData)
                                setBody(createMultiPartFormDataContent(contentType, resourceName))
                            }
                        }

                    Then("the response status should be BadRequest") {
                        response.status shouldBe HttpStatusCode.BadRequest
                    }

                    And("the response body should contain an ApiError with expected validation error") {
                        response.body<ApiError>().validationErrors shouldBe mapOf("image" to expectedError)
                    }
                }
            }

            forAll(
                row(JPEG_CONTENT_TYPE, JPEG_RESOURCE_NAME, false),
                row(PNG_CONTENT_TYPE, PNG_RESOURCE_NAME, false),
                row(PNG_CONTENT_TYPE, PNG_RESOURCE_NAME, true),
            ) { contentType, resourceName, hasExistingImage ->

                When(
                    "posting to /user/image with valid image type $contentType, resource $resourceName, existing image = $hasExistingImage",
                ) {
                    val expectedUserModel =
                        if (hasExistingImage) userModelInTest else userModelInTest.copy(userImage = null)

                    coEvery { userRepository.findByUsername(any()) } returns expectedUserModel
                    coEvery { userImageRepository.saveUserImage(any()) } just Runs
                    coEvery { userImageRepository.updateUserImage(any()) } just Runs

                    val response =
                        withTestApplicationContext { httpClient ->
                            httpClient.post(API_USER_IMAGE) {
                                bearerAuth(tokenStringInTest)
                                contentType(ContentType.MultiPart.FormData)
                                setBody(createMultiPartFormDataContent(contentType, resourceName))
                            }
                        }

                    Then("the response status should be Created") {
                        response.status shouldBe HttpStatusCode.Created

                        coVerify(exactly = 1) {
                            if (hasExistingImage) {
                                userImageRepository.updateUserImage(any())
                            } else {
                                userImageRepository.saveUserImage(any())
                            }
                        }
                    }
                }
            }

            When("getting /user/image without authentication") {
                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.get(API_USER_IMAGE)
                    }

                Then("the response status should be Unauthorized") {
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }

            When("getting /user/image for user without an image") {
                coEvery {
                    userRepository.findByUsername(any())
                } returns userModelInTest.copy(userImage = null)

                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.get(API_USER_IMAGE) { bearerAuth(tokenStringInTest) }
                    }

                Then("the response status should be NotFound") {
                    response.status shouldBe HttpStatusCode.NotFound

                    coVerify(exactly = 1) {
                        userRepository.findByUsername(USERNAME_IN_TEST)
                    }
                }
            }

            When("getting /user/image for user with an image") {
                coEvery { userRepository.findByUsername(any()) } returns userModelInTest

                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.get(API_USER_IMAGE) { bearerAuth(tokenStringInTest) }
                    }

                Then("the response status should be OK") {
                    response.status shouldBe HttpStatusCode.OK

                    coVerify(exactly = 1) {
                        userRepository.findByUsername(USERNAME_IN_TEST)
                    }
                }

                And("the response should contain a Content-Type header") {
                    response.headers[HttpHeaders.ContentType] shouldContain userImageModelInTest.contentType
                }

                And("the response body should contain a ByteArray with image data") {
                    response.body<ByteArray>().contentEquals(userImageModelInTest.imageData)
                }
            }

            When("deleting /user/image for non-existing user") {
                coEvery { userRepository.findIdByUsername(any()) } returns null

                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.delete(API_USER_IMAGE) { bearerAuth(tokenStringInTest) }
                    }

                Then("the response status should be NotFound") {
                    response.status shouldBe HttpStatusCode.NotFound

                    coVerify { userImageRepository wasNot called }
                }
            }

            When("deleting /user/image for existing user") {
                coEvery { userRepository.findIdByUsername(any()) } returns userModelInTest.id
                coEvery { userImageRepository.deleteById(any()) } just Runs

                val response =
                    withTestApplicationContext { httpClient ->
                        httpClient.delete(API_USER_IMAGE) { bearerAuth(tokenStringInTest) }
                    }

                Then("the response status should be NoContent") {
                    response.status shouldBe HttpStatusCode.NoContent

                    coVerify(exactly = 1) { userImageRepository.deleteById(userModelInTest.id) }
                }
            }
        }
    }) {
    companion object {
        fun createMultiPartFormDataContent(
            contentType: String,
            resourceName: String,
        ) = MultiPartFormDataContent(
            formData {
                append(
                    "image",
                    getResourceAsByteArray("/$resourceName"),
                    Headers.build {
                        append(HttpHeaders.ContentType, ContentType.parse(contentType))
                        append(HttpHeaders.ContentDisposition, "filename=\"$resourceName\"")
                    },
                )
            },
        )
    }
}
