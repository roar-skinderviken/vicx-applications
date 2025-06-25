package no.vicx.backend.jwt.github

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row4
import io.kotest.data.forAll
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import no.vicx.backend.config.JsonCustomizerConfig
import no.vicx.backend.jwt.github.GitHubUserFetcher.Companion.SCOPES_HEADER
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.ResponseCreator
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.HttpClientErrorException

@RestClientTest(GitHubUserFetcher::class)
@Import(JsonCustomizerConfig::class)
class GitHubUserFetcherTest(
    mockServer: MockRestServiceServer,
    sut: GitHubUserFetcher
) : BehaviorSpec({

    Given("fetchUser, mockServer with success response") {
        mockServer.expect(requestTo(GitHubUserFetcher.GITHUB_USER_URL))
            .andRespond(createResponseCreator(successBody, true))

        When("calling fetchUser") {
            val gitHubUserResponseVm = sut.fetchUser("~valid-token~")

            Then("the response should be as expected") {
                gitHubUserResponseVm.grantedScopes shouldBe "repo, user"

                assertSoftly(gitHubUserResponseVm.user) {
                    id shouldBe "12345"
                    login shouldBe "john-doe"
                    name shouldBe "John Doe"
                    email shouldBe "john.doe@example.com"
                    avatarUrl shouldBe "https://example.com/avatar.jpg"
                }
            }
        }
    }

    Given("fetchUser, failure responses") {
        When("calling fetchUser and response is 401") {
            mockServer.expect(requestTo(GitHubUserFetcher.GITHUB_USER_URL))
                .andRespond(withStatus(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value())))

            val thrown = shouldThrow<HttpClientErrorException> {
                sut.fetchUser("~token~")
            }

            Then("exception message should be as expected") {
                thrown.message shouldBe "401 Unauthorized: [no body]"
            }
        }

        When("calling fetchUser with a non-HttpClientErrorException failure") {
            mockServer.expect(requestTo(GitHubUserFetcher.GITHUB_USER_URL))
                .andRespond { throw RuntimeException("Unexpected error") }

            val thrown = shouldThrow<IllegalStateException> {
                sut.fetchUser("~token~")
            }

            Then("exception message should indicate failure to fetch user") {
                assertSoftly(thrown) {
                    message shouldBe "Failed to fetch user: Unexpected error"
                    cause.shouldNotBeNull()
                    cause shouldBe instanceOf<RuntimeException>()
                    cause?.message shouldBe "Unexpected error"
                }
            }
        }

        forAll(
            Row4("Empty response string", "", true, "User is null"),
            Row4("Empty JSON object response", "{}", true, "User is empty"),
            Row4("JSON object with null field", "{\"id\": null}", true, "User is empty"),
            Row4("Valid body without scopes header", successBody, false, "No scopes header found")
        ) { description, responseBody, addScopesHeader, expectedErrorMessage ->
            When("calling fetchUser: $description") {
                mockServer.expect(requestTo(GitHubUserFetcher.GITHUB_USER_URL))
                    .andRespond(
                        createResponseCreator(responseBody, addScopesHeader)
                    )

                val thrown = shouldThrow<IllegalStateException> {
                    sut.fetchUser("~token~")
                }

                Then("exception message should be as expected") {
                    thrown.message shouldBe expectedErrorMessage
                }
            }
        }
    }
}) {
    companion object {
        private val successBody = """
            {
                "id": "12345",
                "login": "john-doe",
                "name": "John Doe",
                "email": "john.doe@example.com",
                "avatar_url": "https://example.com/avatar.jpg"
            }""".trimIndent()


        private fun createResponseCreator(
            body: String,
            addScopesHeader: Boolean
        ): ResponseCreator = withSuccess(body, MediaType.APPLICATION_JSON).apply {
            if (addScopesHeader) header(SCOPES_HEADER, "repo, user")
        }
    }
}
