package no.vicx.backend.esport

import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import no.vicx.backend.esport.vm.MatchType
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class EsportServiceTest : StringSpec() {
    private val esportClient: EsportClient = mockk()
    private val sut = EsportService(esportClient)

    init {
        "when calling getMatches then expect result with both match types" {
            every { esportClient.getMatches(any()) } answers { Flux.empty() }

            val matches = sut.getMatches()

            StepVerifier
                .create(matches)
                .expectNextCount(1)
                .verifyComplete()

            @Suppress("ReactiveStreamsUnusedPublisher")
            verifyAll {
                esportClient.getMatches(MatchType.RUNNING)
                esportClient.getMatches(MatchType.UPCOMING)
            }
        }
    }
}
