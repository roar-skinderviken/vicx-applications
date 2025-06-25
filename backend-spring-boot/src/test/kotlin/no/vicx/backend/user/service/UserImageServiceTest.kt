package no.vicx.backend.user.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import no.vicx.backend.error.NotFoundException
import no.vicx.backend.user.UserTestUtils.createMockMultipartFile
import no.vicx.backend.user.UserTestUtils.createValidVicxUser
import no.vicx.database.user.UserImageRepository
import no.vicx.database.user.UserRepository
import no.vicx.database.user.VicxUser
import org.springframework.http.MediaType

class UserImageServiceTest :
    BehaviorSpec({
        val userService: UserService = mockk()
        val userRepository: UserRepository = mockk()
        val userImageRepository: UserImageRepository = mockk(relaxed = true)

        val sut = UserImageService(userService, userRepository, userImageRepository)

        beforeContainer {
            clearAllMocks()
        }

        Given("addOrReplaceUserImage, existing user") {
            val savedUserSlot = slot<VicxUser>()
            val userInTest = spyk(createValidVicxUser())
            val fileInTest =
                spyk(
                    createMockMultipartFile(
                        "test-png.png",
                        MediaType.IMAGE_PNG_VALUE,
                    ),
                )

            beforeContainer {
                every { userService.getUserByUserName(any()) } returns userInTest

                every { userRepository.save(capture(savedUserSlot)) } answers {
                    savedUserSlot.captured.apply { id = 42L }
                }
            }

            When("calling addOrReplaceUserImage") {
                sut.addOrReplaceUserImage(fileInTest, userInTest.username)

                Then("expect image to be added to user") {
                    savedUserSlot.captured.userImage shouldNotBe null

                    verifyOrder {
                        userService.getUserByUserName(any())
                        fileInTest.bytes
                        fileInTest.contentType
                        userRepository.save(userInTest)
                    }
                }
            }
        }

        Given("addOrReplaceUserImage, non-existing user") {
            val usernameInTest = createValidVicxUser().username

            beforeContainer {
                every {
                    userService.getUserByUserName(any())
                } throws NotFoundException("User $usernameInTest not found")
            }

            When("calling addOrReplaceUserImage") {
                val thrown =
                    shouldThrow<NotFoundException> {
                        sut.addOrReplaceUserImage(
                            file = createMockMultipartFile("test-png.png", MediaType.IMAGE_PNG_VALUE),
                            username = usernameInTest,
                        )
                    }

                Then("exception message should be as expected") {
                    thrown.message shouldBe "User $usernameInTest not found"

                    verify(exactly = 0) { userRepository.save(any()) }
                }
            }
        }

        Given("deleteUserImage") {
            When("calling deleteUserImage") {
                sut.deleteUserImage("user1")

                Then("expect image to be deleted") {
                    verify { userImageRepository.deleteByUserUsername(any()) }
                }
            }
        }
    })
