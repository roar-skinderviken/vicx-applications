package no.vicx.util

import no.vicx.db.model.UserImage
import no.vicx.db.model.VicxUser
import no.vicx.util.SecurityTestUtils.USERNAME_IN_TEST

object MiscTestUtils {

    const val GIF_RESOURCE_NAME = "test-gif.gif"
    const val JPEG_RESOURCE_NAME = "test-jpg.jpg"
    const val PNG_RESOURCE_NAME = "test-png.png"
    const val TEXT_RESOURCE_NAME = "test-text.txt"
    const val TOO_LARGE_RESOURCE_NAME = "too-large.png"

    const val GIF_CONTENT_TYPE = "image/gif"
    const val PNG_CONTENT_TYPE = "image/png"
    const val JPEG_CONTENT_TYPE = "image/jpeg"

    const val VALID_PLAINTEXT_PASSWORD: String = "P4ssword"
    const val VALID_BCRYPT_PASSWORD: String = "$2a$10\$sOuu7.j.dOykTbMoXwQpgulTjqUf0EutXqEj8YcZrsNkIzlyZGIry"

    val userImageModelInTest = UserImage(
        id = 1L,
        contentType = "image/png",
        imageData = "~imageData~".toByteArray()
    )

    val userModelInTest = VicxUser(
        id = 1L,
        username = USERNAME_IN_TEST,
        name = "~name~",
        password = VALID_BCRYPT_PASSWORD,
        email = "john.doe@example.com",
        userImage = userImageModelInTest
    )

    fun getResourceAsByteArray(path: String): ByteArray {
        val resourceAsStream = javaClass.getResourceAsStream(path)
        requireNotNull(resourceAsStream)
        return resourceAsStream.readBytes()
    }
}