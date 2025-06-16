package no.vicx.backend.user

import no.vicx.backend.user.vm.CreateUserVm
import no.vicx.database.user.VicxUser
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockMultipartFile

object UserTestUtils {

    fun createValidVicxUser() = VicxUser(
        "user1",
        VicxUser.VALID_BCRYPT_PASSWORD,
        "The User",
        "user@example.com",
        null
    )

    val VALID_USER_VM = CreateUserVm(
        username = "user1",
        password = VicxUser.VALID_PLAINTEXT_PASSWORD,
        name = "The User",
        email = "user@example.com",
        recaptchaToken = "mock-token"
    )

    fun createMockMultipartFile(
        fileName: String,
        fileContentType: String
    ) = MockMultipartFile(
        "image",
        fileName,
        fileContentType,
        ClassPathResource(fileName).inputStream
    )
}