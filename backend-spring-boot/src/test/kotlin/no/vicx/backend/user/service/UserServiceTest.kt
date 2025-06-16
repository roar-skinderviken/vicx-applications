package no.vicx.backend.user.service

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row1
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.vicx.backend.error.NotFoundException
import no.vicx.backend.user.UserTestUtils.VALID_USER_VM
import no.vicx.backend.user.UserTestUtils.createMockMultipartFile
import no.vicx.backend.user.UserTestUtils.createValidVicxUser
import no.vicx.backend.user.vm.ChangePasswordVm
import no.vicx.backend.user.vm.UserPatchVm
import no.vicx.database.user.UserRepository
import no.vicx.database.user.VicxUser
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional

class UserServiceTest : BehaviorSpec({
    val userRepository: UserRepository = mockk()
    val passwordEncoder: PasswordEncoder = mockk()
    val cacheManager: CacheManager = mockk()
    val reCaptchaCache: Cache = mockk(relaxed = true)

    lateinit var sut: UserService

    val userSlot = slot<VicxUser>()

    beforeContainer {
        every { cacheManager.getCache("RECAPTCHA_TOKENS") } returns reCaptchaCache
        every { userRepository.findByUsername("user1") } returns Optional.of(createValidVicxUser())
        every { passwordEncoder.encode(VicxUser.VALID_PLAINTEXT_PASSWORD) } returns VicxUser.VALID_BCRYPT_PASSWORD
        every { userRepository.save(capture(userSlot)) } answers {
            userSlot.captured
        }

        sut = UserService(
            userRepository = userRepository,
            passwordEncoder = passwordEncoder,
            cacheManager = cacheManager,
        )
    }

    Given("UserService construction without reCAPTCHA cache") {
        beforeContainer {
            every { cacheManager.getCache("RECAPTCHA_TOKENS") } returns null
        }

        When("constructing UserService") {
            val thrown = shouldThrow<IllegalArgumentException> {
                UserService(
                    userRepository = userRepository,
                    passwordEncoder = passwordEncoder,
                    cacheManager = cacheManager,
                )
            }

            Then("exception should contain expected message") {
                thrown.message shouldBe "Cache 'RECAPTCHA_TOKENS' is not configured. Application cannot start."
            }
        }
    }

    Given("createUser") {
        When("creating a new user without user image") {
            sut.createUser(VALID_USER_VM, null)

            Then("should create a new user") {
                assertSoftly(userSlot.captured) {
                    name shouldBe VALID_USER_VM.name
                    userImage shouldBe null
                }

                verify {
                    passwordEncoder.encode(VicxUser.VALID_PLAINTEXT_PASSWORD)
                    reCaptchaCache.evictIfPresent(any())
                }
            }
        }

        When("creating a new user with user image") {
            sut.createUser(
                createUserVm = VALID_USER_VM,
                image = createMockMultipartFile(
                    "test-png.png",
                    MediaType.IMAGE_PNG_VALUE
                )
            )

            Then("should create a new user") {
                userSlot.captured.userImage shouldNotBe null

                verify {
                    passwordEncoder.encode(VicxUser.VALID_PLAINTEXT_PASSWORD)
                    reCaptchaCache.evictIfPresent(any())
                }
            }
        }
    }

    Given("getUserByUserName") {
        When("getting user by username for existing user") {
            val user = sut.getUserByUserName("user1")

            Then("it should return a User") {
                user shouldNotBe null
            }
        }

        When("getting user by username for non-existing user") {
            every { userRepository.findByUsername("user1") } returns Optional.empty()

            val thrown = shouldThrow<NotFoundException> {
                sut.getUserByUserName("user1")
            }

            Then("expect user not found error") {
                thrown.message shouldBe "User user1 not found"
            }
        }
    }

    Given("updateUser") {
        val patchVm = UserPatchVm("~name~", "foo@bar.com")

        When("updating user") {
            sut.updateUser(patchVm, "user1")

            Then("expect user to be updated") {
                assertSoftly(userSlot.captured) {
                    name shouldBe patchVm.name
                    email shouldBe patchVm.email
                }

                verify { userRepository.save(any()) }
            }
        }
    }

    Given("isValidPassword") {
        forAll(
            Row1(true),
            Row1(false)
        ) { isValidPassword ->
            every { passwordEncoder.matches(any(), any()) } returns isValidPassword

            When("checking if password is valid: $isValidPassword") {
                val result = sut.isValidPassword("user1", "~password~")

                Then("result should be") {
                    result shouldBe isValidPassword

                    verify { passwordEncoder.matches(any(), any()) }
                }
            }
        }
    }

    Given("updatePassword") {
        val changePasswordVm = ChangePasswordVm(
            currentPassword = "~current-password~",
            password = VicxUser.VALID_PLAINTEXT_PASSWORD
        )

        When("updating password") {
            sut.updatePassword(changePasswordVm, "user1")

            Then("expect user to be updated") {
                verify {
                    passwordEncoder.encode(changePasswordVm.password)
                    userRepository.save(any())
                }
            }
        }
    }
})
