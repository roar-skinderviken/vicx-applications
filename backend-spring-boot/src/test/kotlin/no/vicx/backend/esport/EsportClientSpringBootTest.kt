package no.vicx.backend.esport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk
import no.vicx.backend.esport.vm.EsportMatchVm
import no.vicx.backend.esport.vm.MatchType
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue

@SpringBootTest
class EsportClientSpringBootTest(
    objectMapper: ObjectMapper,
    builder: WebClient.Builder,
) : StringSpec({

        val exchangeFunction: ExchangeFunction = mockk()

        val webClientBuilder =
            builder
                .exchangeFunction(exchangeFunction)

        val sut = EsportClient(webClientBuilder, "~token~")

        // disabled for now until this issue is resolved
        // https://stackoverflow.com/questions/79668983/spring-reactive-webclient-is-not-respecting-kotlinfeature-nullissameasdefault
        "getMatches when matches have null values then expect result"
            .config(enabled = false) {
                val mockJsonWithNullValues =
                    """
                    [{
                        "id": null,
                        "name": null,
                        "begin_at": "",
                        "status": null
                    }]
                    """.trimIndent()

                // this is working
                val parsed = objectMapper.readValue<List<EsportMatchVm>>(mockJsonWithNullValues)
                parsed.shouldNotBeNull()

                every { exchangeFunction.exchange(any()) } answers {
                    Mono.just(
                        createClientResponse(mockJsonWithNullValues),
                    )
                }

                val runningMatches = sut.getMatches(MatchType.RUNNING)

                val expectedMatch =
                    EsportMatchVm(
                        id = null,
                        name = "",
                        beginAt = "",
                        status = "",
                    )

                StepVerifier
                    .create(runningMatches)
                    // org.springframework.core.codec.DecodingException: JSON decoding error
                    .expectNext(expectedMatch)
                    .verifyComplete()
            }
    }) {
    companion object {
        private fun createClientResponse(body: String) =
            ClientResponse
                .create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build()
    }
}
