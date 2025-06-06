package no.vicx.db.repository

import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import no.vicx.db.model.UserImage
import no.vicx.db.model.VicxUser
import no.vicx.util.configureTestDb
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull

class UserRepositoryTest {

    private lateinit var sut: UserRepository

    @BeforeEach
    fun setup() = testApplication {
        configureTestDb()
        sut = UserRepository()
    }

    @Test
    fun `given valid user view model with user image when saving user expect saved user`() = testApplication {

        application {
            val insertedUser = runBlocking { sut.createUser(userModelInTest) }

            assertEquals(
                userModelInTest.copy(
                    id = insertedUser.id,
                    userImage = insertedUser.userImage
                ), insertedUser
            )

            assertNotNull(insertedUser.userImage)

            with(insertedUser.userImage!!) {
                assertEquals(insertedUser.id, id)
                assertEquals(userImageModelInTest.contentType, contentType)

                assertTrue(
                    userImageModelInTest.imageData.contentEquals(imageData),
                    "Expected ${userImageModelInTest.imageData} but got $imageData"
                )
            }
        }
    }

    @Test
    fun `given valid user view model without user image when saving user expect saved user`() = testApplication {
        application {
            val insertedUser = runBlocking {
                sut.createUser(
                    userModelInTest.copy(
                        username = "~username2~",
                        userImage = null
                    )
                )
            }
            assertNull(insertedUser.userImage)
        }
    }

    companion object {
        val userImageModelInTest = UserImage(
            contentType = "image/png",
            imageData = "~imageData~".toByteArray()
        )

        val userModelInTest = VicxUser(
            username = "~username~",
            name = "~name~",
            password = "~password~",
            email = "~email~",
            userImage = userImageModelInTest
        )
    }
}


