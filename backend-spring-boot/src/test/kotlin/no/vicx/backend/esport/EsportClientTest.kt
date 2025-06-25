package no.vicx.backend.esport

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import no.vicx.backend.esport.vm.EsportMatchVm
import no.vicx.backend.esport.vm.MatchType
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class EsportClientTest : StringSpec({
    val exchangeFunction: ExchangeFunction = mockk()

    val webClientBuilder = WebClient.builder()
        .exchangeFunction(exchangeFunction)

    val sut = EsportClient(webClientBuilder, "~token~")

    "getMatches when there are matches then expect match in result" {
        every { exchangeFunction.exchange(any()) } answers {
            Mono.just(
                createClientResponse(objectMapper.writeValueAsString(listOf(expectedMatch)))
            )
        }

        val runningMatches = sut.getMatches(MatchType.RUNNING)

        StepVerifier.create(runningMatches)
            .expectNext(expectedMatch)
            .verifyComplete()
    }

    "getMatches when there are no matches then expect empty result" {
        every { exchangeFunction.exchange(any()) } answers {
            Mono.just(createClientResponse("[]"))
        }

        val runningMatches = sut.getMatches(MatchType.UPCOMING)

        StepVerifier.create(runningMatches)
            .verifyComplete()
    }
}) {
    companion object {
        private val objectMapper = ObjectMapper()

        private val expectedMatch = EsportMatchVm(
            42L,
            "Team 1 vs Team 2",
            "01/01/2024",
            "running"
        )

        private fun createClientResponse(body: String): ClientResponse =
            ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build()
    }
}