package no.vicx.ktor.util

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.date.shouldBeWithin
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import no.vicx.ktor.db.model.UserImage
import no.vicx.ktor.db.model.VicxUser
import no.vicx.ktor.error.ApiError
import no.vicx.ktor.plugins.VALIDATION_ERROR
import no.vicx.ktor.util.SecurityTestUtils.USERNAME_IN_TEST
import java.time.Duration
import java.time.LocalDateTime

object MiscTestUtils {
    const val GIF_RESOURCE_NAME = "test-gif.gif"
    const val JPEG_RESOURCE_NAME = "test-jpg.jpg"
    const val PNG_RESOURCE_NAME = "test-png.png"
    const val TOO_LARGE_RESOURCE_NAME = "too-large.png"

    const val GIF_CONTENT_TYPE = "image/gif"
    const val PNG_CONTENT_TYPE = "image/png"
    const val JPEG_CONTENT_TYPE = "image/jpeg"

    const val VALID_PLAINTEXT_PASSWORD: String = "P4ssword"
    const val VALID_BCRYPT_PASSWORD: String = "$2a$10\$sOuu7.j.dOykTbMoXwQpgulTjqUf0EutXqEj8YcZrsNkIzlyZGIry"

    infix fun LocalDateTime.shouldBeCloseTo(expected: LocalDateTime) =
        this
            .shouldBeWithin(Duration.ofSeconds(5), expected)

    val userImageModelInTest =
        UserImage(
            id = 1L,
            contentType = PNG_CONTENT_TYPE,
            imageData = getResourceAsByteArray("/$PNG_RESOURCE_NAME"),
        )

    val userModelInTest =
        VicxUser(
            id = 1L,
            username = USERNAME_IN_TEST,
            name = "~name~",
            password = VALID_BCRYPT_PASSWORD,
            email = "john.doe@example.com",
            userImage = userImageModelInTest,
        )

    fun getResourceAsByteArray(path: String): ByteArray {
        val resourceAsStream = javaClass.getResourceAsStream(path)
        requireNotNull(resourceAsStream)
        return resourceAsStream.readBytes()
    }

    fun assertValidationErrors(
        apiError: ApiError,
        expectedUrl: String,
        expectedValidationErrors: Map<String, String>,
    ) {
        assertSoftly(apiError) {
            status shouldBe HttpStatusCode.BadRequest.value
            url shouldBe expectedUrl
            message shouldBe VALIDATION_ERROR
            validationErrors shouldBe expectedValidationErrors
        }
    }
}
