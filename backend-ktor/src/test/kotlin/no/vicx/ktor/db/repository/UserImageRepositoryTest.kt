package no.vicx.ktor.db.repository

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import no.vicx.ktor.db.entity.UserImageEntity
import no.vicx.ktor.db.entity.VicxUserEntity
import no.vicx.ktor.db.model.VicxUser
import no.vicx.ktor.db.toModel
import no.vicx.ktor.util.MiscTestUtils.JPEG_CONTENT_TYPE
import no.vicx.ktor.util.MiscTestUtils.JPEG_RESOURCE_NAME
import no.vicx.ktor.util.MiscTestUtils.getResourceAsByteArray
import no.vicx.ktor.util.MiscTestUtils.userImageModelInTest
import no.vicx.ktor.util.MiscTestUtils.userModelInTest
import no.vicx.ktor.util.configureTestDb
import no.vicx.ktor.util.insertTestData
import org.jetbrains.exposed.sql.transactions.transaction

class UserImageRepositoryTest :
    BehaviorSpec({
        coroutineTestScope = true

        Given("User Image Repository") {
            lateinit var sut: UserImageRepository

            beforeContainer {
                configureTestDb()
                sut = UserImageRepository()
            }

            When("saving valid user image") {
                lateinit var userInTest: VicxUser

                testApplication {
                    insertTestData {
                        userInTest =
                            VicxUserEntity
                                .new {
                                    username = userModelInTest.username
                                    name = userModelInTest.name
                                    email = userModelInTest.email
                                    password = userModelInTest.password
                                }.toModel()
                    }

                    application {
                        runBlocking { sut.saveUserImage(userImageModelInTest) }

                        transaction {
                            userInTest = VicxUserEntity[userInTest.id].toModel()
                        }
                    }
                }

                Then("expect saved user image to be returned") {
                    assertSoftly(userInTest.userImage.shouldNotBeNull()) {
                        id shouldBe userInTest.id
                        contentType shouldBe userImageModelInTest.contentType
                        userImageModelInTest.imageData.contentEquals(imageData) shouldBe true
                    }
                }
            }

            When("updating user image") {
                lateinit var userInTest: VicxUser
                val expectedImageData = getResourceAsByteArray("/$JPEG_RESOURCE_NAME")

                testApplication {
                    insertTestData {
                        userInTest =
                            VicxUserEntity
                                .new {
                                    username = userModelInTest.username
                                    name = userModelInTest.name
                                    email = userModelInTest.email
                                    password = userModelInTest.password
                                }.toModel()

                        UserImageEntity.new(userInTest.id) {
                            this.contentType = userImageModelInTest.contentType
                            this.imageData = userImageModelInTest.imageData
                        }
                    }

                    application {
                        runBlocking {
                            sut.updateUserImage(
                                userImageModelInTest.copy(
                                    contentType = JPEG_CONTENT_TYPE,
                                    imageData = expectedImageData,
                                ),
                            )
                        }

                        transaction {
                            userInTest = VicxUserEntity[userInTest.id].toModel()
                        }
                    }
                }

                Then("expect user image to be updated") {
                    assertSoftly(userInTest.userImage.shouldNotBeNull()) {
                        id shouldBe userInTest.id
                        contentType shouldBe JPEG_CONTENT_TYPE
                        expectedImageData.contentEquals(imageData) shouldBe true
                    }
                }
            }

            When("deleting user image") {
                lateinit var userInTest: VicxUser

                testApplication {
                    insertTestData {
                        userInTest =
                            VicxUserEntity
                                .new {
                                    username = userModelInTest.username
                                    name = userModelInTest.name
                                    email = userModelInTest.email
                                    password = userModelInTest.password
                                }.toModel()

                        UserImageEntity.new(userInTest.id) {
                            this.contentType = userImageModelInTest.contentType
                            this.imageData = userImageModelInTest.imageData
                        }
                    }

                    application {
                        runBlocking { sut.deleteById(userInTest.id) }

                        transaction {
                            userInTest = VicxUserEntity[userInTest.id].toModel()
                        }
                    }
                }

                Then("expect user image to be deleted") {
                    userInTest.userImage shouldBe null
                }
            }
        }
    })
