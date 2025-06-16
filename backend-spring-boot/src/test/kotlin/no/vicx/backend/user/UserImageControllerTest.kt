package no.vicx.backend.user

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import no.vicx.backend.BaseWebMvcTest
import no.vicx.backend.SecurityTestUtils.AUTH_HEADER_IN_TEST
import no.vicx.backend.SecurityTestUtils.AUTH_HEADER_IN_TEST_GITHUB
import no.vicx.backend.user.UserTestUtils.createMockMultipartFile
import no.vicx.backend.user.service.UserImageService
import no.vicx.database.user.UserImage
import no.vicx.database.user.UserImageRepository
import org.hamcrest.Matchers.`is`
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional

@WebMvcTest(UserImageController::class)
class UserImageControllerTest(
    @MockkBean(relaxed = true) private val userImageService: UserImageService,
    @MockkBean private val userImageRepository: UserImageRepository
) : BaseWebMvcTest({

    val validImageFile = createMockMultipartFile("test-png.png", MediaType.IMAGE_PNG_VALUE)

    Given("POST /api/user/image valid request") {
        When("performing POST with auth and image") {
            val resultActions = mockMvc.perform(buildMultipartRequest(validImageFile, true))

            Then("expect Created") {
                resultActions.andExpect(status().isCreated)

                verify { userImageService.addOrReplaceUserImage(any(), any()) }
            }
        }
    }

    Given("POST /api/user/image, invalid requests") {
        When("performing POST without auth") {
            val resultActions = mockMvc.perform(buildMultipartRequest(validImageFile, false))

            Then("expect Unauthorized") {
                resultActions.andExpect(status().isUnauthorized())
            }
        }

        When("performing POST with auth but without required role") {
            val resultActions = mockMvc.perform(
                multipart("/api/user/image").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST_GITHUB)
            )

            Then("expect Forbidden") {
                resultActions.andExpect(status().isForbidden)
            }
        }

        When("performing POST with invalid MIME-type (GIF)") {
            val gifFile = createMockMultipartFile("test-gif.gif", MediaType.IMAGE_GIF_VALUE)
            val resultActions = mockMvc.perform(buildMultipartRequest(gifFile, true))

            Then("expect BadRequest and validation error") {
                resultActions.andExpect(status().isBadRequest)
                    .andExpect(
                        jsonPath(
                            "$.validationErrors.image",
                            `is`("Only PNG and JPG files are allowed")
                        )
                    )
            }
        }

        When("performing POST with too large file") {
            val gifFile = createMockMultipartFile("too-large.png", MediaType.IMAGE_PNG_VALUE)
            val resultActions = mockMvc.perform(buildMultipartRequest(gifFile, true))

            Then("expect BadRequest and validation error") {
                resultActions.andExpect(status().isBadRequest)
                    .andExpect(
                        jsonPath(
                            "$.validationErrors.image",
                            `is`("File size exceeds the maximum allowed size of 51200 bytes")
                        )
                    )
            }
        }

        When("performing POST without image file") {
            val resultActions = mockMvc.perform(buildMultipartRequest(null, true))

            Then("expect BadRequest and validation error") {
                resultActions.andExpect(status().isBadRequest)
                    .andExpect(
                        jsonPath(
                            "$.validationErrors.image",
                            `is`("Cannot be null")
                        )
                    )
            }
        }
    }

    Given("GET /api/user/image") {
        When("performing GET without auth") {
            val resultActions = mockMvc.perform(get("/api/user/image"))

            Then("expect Unauthorized") {
                resultActions.andExpect(status().isUnauthorized)
            }
        }

        When("performing GET with auth and image in db") {
            val userImage = UserImage(byteArrayOf(1, 2, 3), MediaType.IMAGE_JPEG_VALUE)
            every { userImageRepository.findByUserUsername(any()) } returns Optional.of(userImage)

            val resultActions = mockMvc
                .perform(get("/api/user/image").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST))

            Then("expect OK") {
                resultActions.andExpect(status().isOk)
            }
        }

        When("performing GET without image in db") {
            every { userImageRepository.findByUserUsername(any()) } returns Optional.empty()

            val resultActions = mockMvc
                .perform(get("/api/user/image").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST))

            Then("expect NotFound") {
                resultActions.andExpect(status().isNotFound)
            }
        }
    }

    Given("DELETE /api/user/image") {
        When("performing DELETE without auth") {
            val resultActions = mockMvc.perform(delete("/api/user/image"))

            Then("expect Unauthorized") {
                resultActions.andExpect(status().isUnauthorized)
            }
        }

        When("performing DELETE with auth") {
            val resultActions = mockMvc.perform(
                delete("/api/user/image")
                    .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST)
            )

            Then("expect NoContent") {
                resultActions.andExpect(status().isNoContent)
            }
        }
    }
}) {
    companion object {
        private fun buildMultipartRequest(
            imageFile: MockMultipartFile?,
            addAuth: Boolean
        ): MockHttpServletRequestBuilder = multipart("/api/user/image").apply {
            if (addAuth) header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST)
            if (imageFile != null) {
                file(imageFile).contentType(MediaType.MULTIPART_FORM_DATA)
            }
        }
    }
}