package no.vicx.authserver

import no.vicx.authserver.config.DefaultUserProperties
import no.vicx.database.user.UserImage
import no.vicx.database.user.VicxUser
import org.springframework.http.MediaType

object UserTestUtils {
    const val DEFAULT_USERNAME_IN_TEST = "user1"
    const val EXISTING_USERNAME = "~username~"

    private val userImageInTest = UserImage(byteArrayOf(1, 2, 3), MediaType.IMAGE_PNG_VALUE)

    val defaultUserProperties =
        DefaultUserProperties().apply {
            username = DEFAULT_USERNAME_IN_TEST
            password = "~default-user-password~"
            name = "~default-user-name~"
            email = "~default-user-email~"
        }

    fun customUserDetailsInTest(hasImage: Boolean = true) =
        CustomUserDetails(
            "~username~",
            "~password~",
            "~name~",
            "~email~",
            hasImage,
        )

    fun createUserInTest(userImage: UserImage? = userImageInTest) =
        VicxUser(
            EXISTING_USERNAME,
            VicxUser.VALID_BCRYPT_PASSWORD,
            "~name~",
            "~email~",
            userImage,
        )
}
