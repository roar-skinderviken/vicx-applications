package no.vicx.db.repository

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import no.vicx.db.entity.UserImageEntity
import no.vicx.db.entity.VicxUserEntity
import no.vicx.db.model.VicxUser
import no.vicx.db.repository.UserRepository.Companion.PASSWORD_MUST_BE_ENCRYPTED_MSG
import no.vicx.db.toModel
import no.vicx.util.MiscTestUtils.VALID_PLAINTEXT_PASSWORD
import no.vicx.util.MiscTestUtils.userImageModelInTest
import no.vicx.util.MiscTestUtils.userModelInTest
import no.vicx.util.configureTestDb
import no.vicx.util.insertTestData
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryTest : BehaviorSpec({
    coroutineTestScope = true

    Given("User Repository") {
        lateinit var sut: UserRepository

        beforeContainer {
            configureTestDb()
            sut = UserRepository()
        }

        When("saving user with plain-text password") {
            val thrown = shouldThrow<IllegalArgumentException> {
                sut.createUser(userModelInTest.copy(password = VALID_PLAINTEXT_PASSWORD))
            }

            Then("throws exception") {
                thrown.message shouldBe PASSWORD_MUST_BE_ENCRYPTED_MSG
            }
        }

        When("saving valid user with user image") {
            lateinit var insertedUser: VicxUser

            testApplication {
                application {
                    insertedUser = runBlocking { sut.createUser(userModelInTest) }
                }
            }

            Then("expect saved user to be returned") {
                insertedUser.userImage shouldNotBe null
                insertedUser shouldBe userModelInTest

                assertSoftly(insertedUser.userImage!!) {
                    id shouldBe insertedUser.id
                    contentType shouldBe userImageModelInTest.contentType
                    userImageModelInTest.imageData.contentEquals(imageData) shouldBe true
                }
            }
        }

        When("saving valid user without user image") {
            lateinit var insertedUser: VicxUser

            testApplication {
                application {
                    insertedUser = runBlocking {
                        sut.createUser(
                            userModelInTest.copy(
                                username = "~username2~",
                                userImage = null
                            )
                        )
                    }
                }
            }

            Then("expect saved user to be returned without image") {
                insertedUser.userImage shouldBe null
            }
        }

        When("calling findByUsername with non-existing username expect null") {
            testApplication {
                application {
                    runBlocking {
                        sut.findByUsername("~non-existing-username~")
                    }.shouldBeNull()
                }
            }
        }

        When("calling findByUsername with existing username") {
            lateinit var fetchedUser: VicxUser

            testApplication {
                insertTestData {
                    val insertedUser = VicxUserEntity.new {
                        username = userModelInTest.username
                        name = userModelInTest.name
                        email = userModelInTest.email
                        password = userModelInTest.password
                    }

                    UserImageEntity.new(insertedUser.id.value) {
                        this.contentType = userImageModelInTest.contentType
                        this.imageData = userImageModelInTest.imageData
                    }
                }

                application {
                    runBlocking {
                        fetchedUser = sut.findByUsername(userModelInTest.username)!!
                    }
                }
            }

            Then("expect findByUsername to be returned") {
                assertSoftly(fetchedUser) {
                    username shouldBe userModelInTest.username
                    name shouldBe userModelInTest.name
                    email shouldBe userModelInTest.email
                    password shouldBe userModelInTest.password
                    userImage shouldNotBe null
                }
            }
        }

        forAll(
            row("Both values changed", "~new-name~", "~new-email~"),
            row("Name changed", "~new-name~", null),
            row("Email changed", null, "~new-email~"),
            row("No values changed", null, null),
        ) { description, newName, newEmail ->
            When("calling update user, $description") {
                lateinit var insertedUser: VicxUser
                lateinit var updatedUser: VicxUser

                testApplication {
                    insertTestData {
                        insertedUser = VicxUserEntity.new {
                            username = userModelInTest.username
                            name = userModelInTest.name
                            email = userModelInTest.email
                            password = userModelInTest.password
                        }.toModel()
                    }

                    application {
                        runBlocking {
                            sut.updateUser(insertedUser.id, newName, newEmail)
                        }

                        transaction {
                            updatedUser = VicxUserEntity[1L].toModel()
                        }
                    }
                }

                Then("expect user to be updated if any non-null values") {
                    assertSoftly(updatedUser) {
                        name shouldBe (newName ?: userModelInTest.name)
                        email shouldBe (newEmail ?: userModelInTest.email)
                    }
                }
            }
        }
    }
})



