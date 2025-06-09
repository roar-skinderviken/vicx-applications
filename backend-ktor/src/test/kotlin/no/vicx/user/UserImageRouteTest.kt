package no.vicx.user

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.mockk.*
import no.vicx.db.model.UserImage
import no.vicx.error.ApiError
import no.vicx.user.UserTestConstants.API_USER_IMAGE
import no.vicx.util.MiscTestUtils.GIF_CONTENT_TYPE
import no.vicx.util.MiscTestUtils.GIF_RESOURCE_NAME
import no.vicx.util.MiscTestUtils.JPEG_CONTENT_TYPE
import no.vicx.util.MiscTestUtils.JPEG_RESOURCE_NAME
import no.vicx.util.MiscTestUtils.PNG_CONTENT_TYPE
import no.vicx.util.MiscTestUtils.PNG_RESOURCE_NAME
import no.vicx.util.MiscTestUtils.TOO_LARGE_RESOURCE_NAME
import no.vicx.util.MiscTestUtils.getResourceAsByteArray
import no.vicx.util.RouteTestContext
import no.vicx.util.SecurityTestUtils.USERNAME_IN_TEST
import no.vicx.util.SecurityTestUtils.tokenStringInTest

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
            row(
                GIF_CONTENT_TYPE, GIF_RESOURCE_NAME,
                "Image file type: Only PNG and JPG files are allowed"
            ),
            row(
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
            row(JPEG_CONTENT_TYPE, JPEG_RESOURCE_NAME),
            row(PNG_CONTENT_TYPE, PNG_RESOURCE_NAME),
        ) { contentType, resourceName ->

            When("calling POST /user/image with valid $contentType, $resourceName") {
                val userImageSlot = slot<UserImage>()
                coEvery {
                    routeTestContext.userImageService.addOrReplaceUserImage(
                        capture(userImageSlot),
                        any()
                    )
                } just Runs

                val response = routeTestContext.runInTestApplicationContext { httpClient ->
                    httpClient.post(API_USER_IMAGE) {
                        bearerAuth(tokenStringInTest)
                        contentType(ContentType.MultiPart.FormData)
                        setBody(createMultiPartFormDataContent(contentType, resourceName))
                    }
                }

                Then("expect Created") {
                    response.status shouldBe HttpStatusCode.Created

                    assertSoftly(userImageSlot.captured) {
                        contentType shouldBe contentType
                        imageData.contentEquals(getResourceAsByteArray("/$resourceName")) shouldBe true
                    }

                    coVerify(exactly = 1) {
                        routeTestContext.userImageService.addOrReplaceUserImage(any(), USERNAME_IN_TEST)
                    }
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