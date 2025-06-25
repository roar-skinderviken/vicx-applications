package no.vicx.authserver.config

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.vicx.authserver.CustomUserDetails
import no.vicx.authserver.UserTestUtils.DEFAULT_USERNAME_IN_TEST
import no.vicx.authserver.UserTestUtils.EXISTING_USERNAME
import no.vicx.authserver.UserTestUtils.createUserInTest
import no.vicx.authserver.UserTestUtils.defaultUserProperties
import no.vicx.database.user.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional

class UserDetailsServiceTest :
    BehaviorSpec({

        Given("UserDetails") {
            val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
            val userRepository: UserRepository = mockk()

            val sut =
                UserDetailsConfig().userDetailsService(
                    userProperties = defaultUserProperties,
                    passwordEncoder = passwordEncoder,
                    userRepository = userRepository,
                )

            When("loadUserByUsername with default username") {
                sut.loadUserByUsername(DEFAULT_USERNAME_IN_TEST)

                Then("expect no calls to repository") {
                    verify { userRepository wasNot called }
                }
            }

            When("loadUserByUsername with username for non-existing user") {
                every { userRepository.findByUsername(any()) } returns Optional.empty()

                val thrown =
                    shouldThrow<UsernameNotFoundException> {
                        sut.loadUserByUsername("~unknown-username~")
                    }

                Then("exception should be as expected") {
                    thrown.message shouldBe "User not found"
                }
            }

            forAll(
                row("User with image", createUserInTest()),
                row("User without image", createUserInTest(null)),
            ) { description, userInTest ->

                When("loadUserByUsername with username for existing user, $description") {
                    every { userRepository.findByUsername(EXISTING_USERNAME) } returns Optional.of(userInTest)

                    val userDetails = sut.loadUserByUsername(EXISTING_USERNAME)

                    Then("userDetails should be as expected") {
                        userDetails.shouldBeInstanceOf<CustomUserDetails>()

                        assertSoftly(userDetails) {
                            username shouldBe userInTest.username
                            password shouldBe userInTest.password
                            name shouldBe userInTest.name
                            email shouldBe userInTest.email
                            hasImage shouldBe (userInTest.userImage != null)
                        }
                    }
                }
            }
        }
    })
