package no.vicx.backend.jwt.github.vm

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import no.vicx.backend.jwt.github.GitHubTestUtils.githubUserInTest
import org.springframework.security.core.authority.SimpleGrantedAuthority

class GitHubUserResponseVmTest : BehaviorSpec({

    Given("a GitHubUserResponseVm with all fields set") {
        val sut = GitHubUserResponseVm(
            user = githubUserInTest,
            grantedScopes = "~granted-scopes~",
            token = "~token~"
        )

        When("calling toPrincipal") {
            val principal = sut.toPrincipal()

            Then("principal contains correct name and roles") {
                assertSoftly(principal) {
                    name shouldBe githubUserInTest.login
                    authorities shouldContain SimpleGrantedAuthority("ROLE_GITHUB_USER")
                }
            }
        }
    }
})