package no.vicx.backend.jwt.github.vm

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row1
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe

class GitHubUserVmTest :
    BehaviorSpec({

        Given("a GitHubUserVm with all values set to empty") {
            val sut = GitHubUserVm("", "", "", "", "")

            When("calling isEmpty") {
                val isEmpty = sut.isEmpty

                Then("It should be empty") {
                    isEmpty shouldBe true
                }
            }
        }

        Given("a GitHubUserVm with at least one value") {
            forAll(
                Row1(GitHubUserVm("~id~", "", "", "", "")),
                Row1(GitHubUserVm("", "~login~", "", "", "")),
                Row1(GitHubUserVm("", "", "~name~", "", "")),
                Row1(GitHubUserVm("", "", "", "~email~", "")),
                Row1(GitHubUserVm("", "", "", "", "~avatar~")),
            ) { sut ->

                When("calling isEmpty: $sut") {
                    val isEmpty = sut.isEmpty

                    Then("It should not be empty") {
                        isEmpty shouldBe false
                    }
                }
            }
        }
    })
