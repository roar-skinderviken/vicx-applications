package no.vicx.ktor.db.repository

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.vicx.ktor.db.entity.UserImageEntity
import no.vicx.ktor.db.entity.VicxUserEntity
import no.vicx.ktor.db.toModel
import no.vicx.ktor.util.MiscTestUtils.JPEG_CONTENT_TYPE
import no.vicx.ktor.util.MiscTestUtils.JPEG_RESOURCE_NAME
import no.vicx.ktor.util.MiscTestUtils.getResourceAsByteArray
import no.vicx.ktor.util.MiscTestUtils.userImageModelInTest
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.configureTestDb
import org.jetbrains.exposed.sql.transactions.transaction

class UserImageRepositoryTest :
    BehaviorSpec({
        val sut = UserImageRepository()

        Given("User Image Repository") {
            beforeContainer {
                configureTestDb()
            }

            When("saving a valid user image") {
                var userInTest =
                    transaction {
                        VicxUserEntity
                            .new {
                                username = userModelInTest.username
                                name = userModelInTest.name
                                email = userModelInTest.email
                                password = userModelInTest.password
                            }.toModel()
                    }

                sut.saveUserImage(userImageModelInTest)

                Then("it should save the user with user image in db") {
                    val userInDb =
                        transaction {
                            VicxUserEntity[userInTest.id].toModel()
                        }

                    assertSoftly(userInDb.userImage.shouldNotBeNull()) {
                        id shouldBe userInTest.id
                        contentType shouldBe userImageModelInTest.contentType
                        userImageModelInTest.imageData.contentEquals(imageData) shouldBe true
                    }
                }
            }

            When("updating user image") {
                var userIdInTest = 0L
                val expectedImageData = getResourceAsByteArray("/$JPEG_RESOURCE_NAME")

                transaction {
                    userIdInTest =
                        VicxUserEntity
                            .new {
                                username = userModelInTest.username
                                name = userModelInTest.name
                                email = userModelInTest.email
                                password = userModelInTest.password
                            }.id.value

                    UserImageEntity.new(userIdInTest) {
                        this.contentType = userImageModelInTest.contentType
                        this.imageData = userImageModelInTest.imageData
                    }
                }

                sut.updateUserImage(
                    userImageModelInTest.copy(
                        contentType = JPEG_CONTENT_TYPE,
                        imageData = expectedImageData,
                    ),
                )

                Then("it should update the user image in db") {
                    val userInDb =
                        transaction {
                            VicxUserEntity[userIdInTest].toModel()
                        }

                    assertSoftly(userInDb.userImage.shouldNotBeNull()) {
                        id shouldBe userIdInTest
                        contentType shouldBe JPEG_CONTENT_TYPE
                        expectedImageData.contentEquals(imageData) shouldBe true
                    }
                }
            }

            When("deleting user image") {
                var userIdInTest = 0L

                transaction {
                    userIdInTest =
                        VicxUserEntity
                            .new {
                                username = userModelInTest.username
                                name = userModelInTest.name
                                email = userModelInTest.email
                                password = userModelInTest.password
                            }.id.value

                    UserImageEntity.new(userIdInTest) {
                        this.contentType = userImageModelInTest.contentType
                        this.imageData = userImageModelInTest.imageData
                    }
                }

                sut.deleteById(userIdInTest)

                Then("it should delete the user image in db") {
                    transaction {
                        VicxUserEntity[userIdInTest].userImage.shouldBeNull()
                    }
                }
            }
        }
    })
