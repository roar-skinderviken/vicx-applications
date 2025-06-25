package no.vicx.backend.jwt.github

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.vicx.backend.jwt.github.GitHubTestUtils.githubUserInTest
import no.vicx.backend.jwt.github.vm.GitHubUserResponseVm
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException
import org.springframework.web.client.HttpClientErrorException

class GitHubFromOpaqueProducerTest :
    BehaviorSpec({
        val githubUserFetcher: GitHubUserFetcher = mockk()
        val sut = GitHubFromOpaqueProducer(githubUserFetcher)

        Given("createPrincipal, valid GitHub token response") {
            every { githubUserFetcher.fetchUser(any()) } returns
                GitHubUserResponseVm(
                    user = githubUserInTest,
                    grantedScopes = "repo, user",
                    token = "valid-token",
                )

            When("calling createPrincipal with valid token") {
                val principal = sut.createPrincipal("~valid-token~")

                Then("it should return the principal") {
                    principal.name shouldBe githubUserInTest.login
                }
            }
        }

        Given("createPrincipal, failure scenarios") {
            When("calling createPrincipal with invalid token") {
                every { githubUserFetcher.fetchUser(any()) } throws
                    HttpClientErrorException.create(
                        HttpStatus.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED.reasonPhrase,
                        HttpHeaders.EMPTY,
                        "".toByteArray(),
                        null,
                    )

                val thrown =
                    shouldThrow<BadOpaqueTokenException> {
                        sut.createPrincipal("~invalid-token~")
                    }

                Then("exception should contain invalid token message") {
                    thrown.message shouldBe "Invalid or expired GitHub access token"
                }
            }

            When("an unknown exception is thrown when calling createPrincipal") {
                every { githubUserFetcher.fetchUser(any()) } throws RuntimeException("~some-error~")

                val thrown =
                    shouldThrow<BadOpaqueTokenException> {
                        sut.createPrincipal("~valid-token~")
                    }

                Then("exception should contain generic error message") {
                    thrown.message shouldBe "Error validating GitHub token"
                }
            }
        }
    })
