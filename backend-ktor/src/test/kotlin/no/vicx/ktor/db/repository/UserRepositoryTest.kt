package no.vicx.ktor.db.repository

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.vicx.ktor.db.entity.UserImageEntity
import no.vicx.ktor.db.entity.VicxUserEntity
import no.vicx.ktor.db.model.VicxUser
import no.vicx.ktor.db.repository.UserRepository.Companion.PASSWORD_MUST_BE_ENCRYPTED_MSG
import no.vicx.ktor.db.toModel
import no.vicx.ktor.util.MiscTestUtils.VALID_PLAINTEXT_PASSWORD
import no.vicx.ktor.util.MiscTestUtils.userImageModelInTest
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.configureTestDb
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryTest :
    BehaviorSpec({
        val sut = UserRepository()

        Given("User Repository") {
            beforeContainer {
                configureTestDb()
            }

            When("saving user with plain-text password") {
                val thrown =
                    shouldThrow<IllegalArgumentException> {
                        sut.createUser(userModelInTest.copy(password = VALID_PLAINTEXT_PASSWORD))
                    }

                Then("it should throw exception with password-must-be-encrypted error") {
                    thrown.message shouldBe PASSWORD_MUST_BE_ENCRYPTED_MSG
                }
            }

            When("saving valid user with user image") {
                lateinit var userInDb: VicxUser

                val insertedUser = sut.createUser(userModelInTest)

                Then("it should save the user in the database") {
                    userInDb =
                        transaction { VicxUserEntity[insertedUser.id].toModel() }
                    userInDb.copy(userImage = null) shouldBe userModelInTest.copy(userImage = null)
                }

                And("it should save the user image in the database") {
                    assertSoftly(userInDb.userImage.shouldNotBeNull()) {
                        id shouldBe insertedUser.id
                        contentType shouldBe userImageModelInTest.contentType
                        userImageModelInTest.imageData.contentEquals(imageData) shouldBe true
                    }
                }
            }

            When("saving valid user without user image") {
                val insertedUser =
                    sut.createUser(
                        userModelInTest.copy(
                            username = "~username2~",
                            userImage = null,
                        ),
                    )

                Then("it should save the user in the database without user image") {
                    val userInDb =
                        transaction { VicxUserEntity[insertedUser.id].toModel() }

                    userInDb.userImage shouldBe null
                }
            }

            When("retrieving a non-existing user by username, it should return null") {
                sut.findByUsername("~non-existing-username~").shouldBeNull()
            }

            When("retrieving an existing user by username") {
                transaction {
                    val insertedUser =
                        VicxUserEntity.new {
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

                val fetchedUser = sut.findByUsername(userModelInTest.username)

                Then("it should return the user from the database") {
                    assertSoftly(fetchedUser.shouldNotBeNull()) {
                        username shouldBe userModelInTest.username
                        name shouldBe userModelInTest.name
                        email shouldBe userModelInTest.email
                        password shouldBe userModelInTest.password
                        userImage shouldNotBe null
                    }
                }
            }

            When("retrieving user-ID by username for existing user") {
                transaction {
                    VicxUserEntity.new {
                        username = userModelInTest.username
                        name = userModelInTest.name
                        email = userModelInTest.email
                        password = userModelInTest.password
                    }
                }

                val fetchedUserId = sut.findIdByUsername(userModelInTest.username)

                Then("it should return the user-ID") {
                    fetchedUserId shouldNotBe null
                }
            }

            forAll(
                Row3("Both values changed", "~new-name~", "~new-email~"),
                Row3("Name changed", "~new-name~", null),
                Row3("Email changed", null, "~new-email~"),
                Row3("No values changed", null, null),
            ) { description, newName, newEmail ->
                When("updating a user, $description") {
                    val insertedUser =
                        transaction {
                            VicxUserEntity
                                .new {
                                    username = userModelInTest.username
                                    name = userModelInTest.name
                                    email = userModelInTest.email
                                    password = userModelInTest.password
                                }.toModel()
                        }

                    sut.updateUser(
                        id = insertedUser.id,
                        name = newName,
                        email = newEmail,
                    )

                    Then("it should save changes in the database") {
                        val userInDb =
                            transaction {
                                VicxUserEntity[1L].toModel()
                            }

                        assertSoftly(userInDb) {
                            name shouldBe (newName ?: userModelInTest.name)
                            email shouldBe (newEmail ?: userModelInTest.email)
                        }
                    }
                }
            }
        }
    })
