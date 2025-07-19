package no.vicx.backend.user

import com.ninjasquad.springmockk.MockkBean
import io.kotest.data.Row2
import io.kotest.data.Row3
import io.kotest.data.forAll
import io.mockk.every
import io.mockk.verify
import no.vicx.backend.BaseWebMvcTest
import no.vicx.backend.user.UserTestUtils.VALID_USER_VM
import no.vicx.backend.user.UserTestUtils.createMockMultipartFile
import no.vicx.backend.user.UserTestUtils.createValidVicxUser
import no.vicx.backend.user.service.RecaptchaService
import no.vicx.backend.user.service.UserService
import no.vicx.backend.user.vm.CreateUserVm
import no.vicx.database.user.UserRepository
import no.vicx.database.user.VicxUser.VALID_PLAINTEXT_PASSWORD
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional

@WebMvcTest(UserController::class)
class UserControllerPostTest(
    @MockkBean private val recaptchaService: RecaptchaService,
    @MockkBean(relaxed = true) private val userService: UserService,
    @MockkBean private val userRepository: UserRepository,
) : BaseWebMvcTest({

        val validVicxUser = createValidVicxUser().apply { id = 42L }

        Given("POST /api/user success") {
            beforeContainer {
                every { userRepository.findByUsername(any()) } returns Optional.empty()
                every { userService.createUser(any(), any()) } returns validVicxUser
                every { recaptchaService.verifyToken(any()) } returns true
            }

            forAll(
                Row2("with user image", createMockMultipartFile("profile.png", MediaType.IMAGE_PNG_VALUE)),
                Row2("without user image", null),
            ) { description, userImage ->
                When("performing POST request: $description") {
                    val resultActions = mockMvc.perform(buildMultipartRequest(VALID_USER_VM, userImage))

                    Then("expect user to be created") {
                        resultActions
                            .andExpect(status().isCreated())
                            .andExpect(header().string(HttpHeaders.LOCATION, "/api/user"))
                            .andExpect(content().string(UserController.USER_CREATED_BODY_TEXT))

                        if (userImage != null) {
                            verify { userService.createUser(isNull(inverse = true), isNull(inverse = true)) }
                        } else {
                            verify { userService.createUser(isNull(inverse = true), isNull()) }
                        }

                        verify { recaptchaService.verifyToken(any()) }
                    }
                }
            }
        }

        Given("POST /api/user failure") {
            When("performing POST request with invalid reCAPTCHA token") {
                every { recaptchaService.verifyToken(any()) } returns false

                val resultActions = mockMvc.perform(buildMultipartRequest(VALID_USER_VM))

                Then("expect BadRequest and validation error") {
                    resultActions
                        .andExpect(status().isBadRequest())
                        .andExpect(
                            jsonPath("$.validationErrors.recaptchaToken")
                                .value("Invalid reCAPTCHA, please wait to token expires and try again"),
                        )
                }
            }

            When("performing POST request for existing username") {
                every { recaptchaService.verifyToken(any()) } returns true
                every { userRepository.findByUsername(any()) } returns Optional.of(validVicxUser)

                val resultActions = mockMvc.perform(buildMultipartRequest(VALID_USER_VM))

                Then("expect BadRequest and validation error") {
                    resultActions
                        .andExpect(status().isBadRequest())
                        .andExpect(
                            jsonPath("$.validationErrors.username")
                                .value("This name is in use"),
                        )
                }
            }

            forAll(
                Row2(
                    "Only PNG and JPG files are allowed",
                    createMockMultipartFile("test-gif.gif", MediaType.IMAGE_GIF_VALUE),
                ),
                Row2(
                    "File size exceeds the maximum allowed size of 51200 bytes",
                    createMockMultipartFile("too-large.png", MediaType.IMAGE_PNG_VALUE),
                ),
            ) { expectedError, image ->

                When("performing POST request with invalid image: $expectedError") {
                    every { recaptchaService.verifyToken(any()) } returns true
                    every { userRepository.findByUsername(any()) } returns Optional.empty()

                    val resultActions = mockMvc.perform(buildMultipartRequest(VALID_USER_VM, image))

                    Then("expect BadRequest and validation error") {
                        resultActions
                            .andExpect(status().isBadRequest())
                            .andExpect(
                                jsonPath("$.validationErrors.image")
                                    .value(expectedError),
                            )
                    }
                }
            }

            forAll(
/*
                Row3(
                    CreateUserVm(null, VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                    "username",
                    "Username cannot be null",
                ),
*/
                Row3(
                    CreateUserVm(" ".repeat(4), VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                    "username",
                    "Username can only contain letters, numbers, hyphens, and underscores",
                ),
                Row3(
                    CreateUserVm("a", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                    "username",
                    "It must have minimum 4 and maximum 255 characters",
                ),
                Row3(
                    CreateUserVm("a".repeat(256), VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                    "username",
                    "It must have minimum 4 and maximum 255 characters",
                ),
                Row3(
                    CreateUserVm("John Doe", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", "mock-token"),
                    "username",
                    "Username can only contain letters, numbers, hyphens, and underscores",
                ),
/*
                Row3(
                    CreateUserVm("user1", null, "The User", "user@example.com", "mock-token"),
                    "password",
                    "Cannot be null",
                ),
*/
                Row3(
                    CreateUserVm("user1", "Aa1Aa1", "The User", "user@example.com", "mock-token"),
                    "password",
                    "It must have minimum 8 and maximum 255 characters",
                ),
                Row3(
                    CreateUserVm("user1", "Aa1".repeat(90), "The User", "user@example.com", "mock-token"),
                    "password",
                    "It must have minimum 8 and maximum 255 characters",
                ),
                Row3(
                    CreateUserVm("user1", "a".repeat(8), "The User", "user@example.com", "mock-token"),
                    "password",
                    "Password must have at least one uppercase, one lowercase letter and one number",
                ),
/*
                Row3(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, null, "user@example.com", "mock-token"),
                    "name",
                    "Cannot be null",
                ),
*/
                Row3(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "a".repeat(3), "user@example.com", "mock-token"),
                    "name",
                    "It must have minimum 4 and maximum 255 characters",
                ),
/*
                Row3(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", null, "mock-token"),
                    "email",
                    "Cannot be null",
                ),
*/
                Row3(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "a", "mock-token"),
                    "email",
                    "It must be a well-formed email address",
                ),
                Row3(
                    CreateUserVm("user1", VALID_PLAINTEXT_PASSWORD, "The User", "user@example.com", ""),
                    "recaptchaToken",
                    "reCAPTCHA cannot be null or blank",
                ),
            ) { createUserVm, fieldName, expectedMessage ->

                When("performing POST request with invalid view model: $createUserVm, $fieldName, $expectedMessage") {
                    every { recaptchaService.verifyToken(any()) } returns true
                    every { userRepository.findByUsername(any()) } returns Optional.empty()

                    val resultActions = mockMvc.perform(buildMultipartRequest(createUserVm))

                    Then("expect BadRequest and validation error") {
                        resultActions
                            .andExpect(status().isBadRequest())
                            .andExpect(
                                jsonPath("$.validationErrors.$fieldName")
                                    .value(expectedMessage),
                            )
                    }
                }
            }
        }
    }) {
    companion object {
        private fun buildMultipartRequest(
            createUserVm: CreateUserVm,
            imageFile: MockMultipartFile? = null,
        ): MockHttpServletRequestBuilder =
            multipart("/api/user").apply {
                this
                    .contentType(MediaType.MULTIPART_FORM_DATA) // not required, just for clarity
                    .param("username", createUserVm.username ?: "")
                    .param("password", createUserVm.password ?: "")
                    .param("email", createUserVm.email ?: "")
                    .param("name", createUserVm.name ?: "")
                    .param("recaptchaToken", createUserVm.recaptchaToken)

                if (imageFile != null) {
                    this.file(imageFile)
                }
            }
    }
}
