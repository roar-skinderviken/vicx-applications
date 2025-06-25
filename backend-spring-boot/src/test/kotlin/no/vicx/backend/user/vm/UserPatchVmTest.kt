package no.vicx.backend.user.vm

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row2
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import no.vicx.database.user.VicxUser

class UserPatchVmTest :
    BehaviorSpec({
        val vicxUser: VicxUser = mockk(relaxed = true)

        beforeContainer {
            clearAllMocks()
        }

        Given("applyPatch, view model with both properties set") {
            val sut = UserPatchVm("~name~", "~email~")

            When("applying patch to user") {
                val updatedUser = sut.applyPatch(vicxUser)

                Then("expect user to be updated") {
                    updatedUser shouldBeSameInstanceAs vicxUser

                    verify {
                        vicxUser.name = "~name~"
                        vicxUser.email = "~email~"
                    }
                }
            }
        }

        Given("applyPatch, empty view model") {
            val sut = UserPatchVm(null, null)

            When("applying patch to user") {
                val updatedUser = sut.applyPatch(vicxUser)

                Then("expect user not to be updated") {
                    updatedUser shouldBeSameInstanceAs vicxUser

                    verify { vicxUser wasNot Called }
                }
            }
        }

        Given("isEmpty") {
            val patchVmBase = UserPatchVm("~name~", "~email~")

            forAll(
                Row2(patchVmBase, false),
                Row2(patchVmBase.copy(name = null), false),
                Row2(patchVmBase.copy(name = " "), false),
                Row2(patchVmBase.copy(email = null), false),
                Row2(patchVmBase.copy(email = " "), false),
                Row2(patchVmBase.copy(name = null, email = null), true),
                Row2(patchVmBase.copy(name = "", email = ""), true),
                Row2(patchVmBase.copy(name = " ", email = " "), true),
            ) { userPatchVm, expectedIsEmpty ->
                Then("calling isEmpty: $userPatchVm") {
                    userPatchVm.isEmpty shouldBe expectedIsEmpty
                }
            }
        }
    })
