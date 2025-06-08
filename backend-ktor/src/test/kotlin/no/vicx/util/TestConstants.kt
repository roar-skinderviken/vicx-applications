package no.vicx.util

import no.vicx.db.model.UserImage
import no.vicx.db.model.VicxUser
import no.vicx.util.SecurityTestUtils.USERNAME_IN_TEST

object TestConstants {

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
}