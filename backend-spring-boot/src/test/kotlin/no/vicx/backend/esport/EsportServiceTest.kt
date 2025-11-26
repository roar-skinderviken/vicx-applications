package no.vicx.backend.esport

import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verifyAll
import no.vicx.backend.esport.vm.MatchType
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class EsportServiceTest : StringSpec() {
    @MockK
    lateinit var esportClient: EsportClient

    @InjectMockKs
    lateinit var sut: EsportService

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
