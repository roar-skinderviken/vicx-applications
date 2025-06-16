package no.vicx.backend.user.vm

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.vicx.database.user.VicxUser
import org.springframework.security.crypto.password.PasswordEncoder

class ChangePasswordVmTest : BehaviorSpec({
    val vicxUser: VicxUser = mockk(relaxed = true)
    val passwordEncoder: PasswordEncoder = mockk()

    Given("valid change password view model") {
        val sut = ChangePasswordVm("~current-password~", "~new-password~")

        val expectedPassword = "~encoded-password~"
        every { passwordEncoder.encode(any()) } returns expectedPassword

        When("applying patch to user") {
            val updatedUser = sut.applyPatch(vicxUser, passwordEncoder)

            Then("expect user to be updated with new password") {
                updatedUser shouldBeSameInstanceAs vicxUser

                verify { vicxUser.password = expectedPassword }
            }
        }
    }
})