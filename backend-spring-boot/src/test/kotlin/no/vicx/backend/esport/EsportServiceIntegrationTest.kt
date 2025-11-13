package no.vicx.backend.esport

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.verify
import no.vicx.backend.esport.vm.EsportVm
import no.vicx.backend.esport.vm.MatchType
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
class EsportServiceIntegrationTest(
    sut: EsportService,
    @MockkBean private val esportClient: EsportClient,
) : StringSpec({
        "when calling getMatches twice then expect result from cache for second call" {
            every { esportClient.getMatches(any()) } answers { Flux.empty() }

            verifyResult(sut.getMatches())
            verifyResult(sut.getMatches())

            @Suppress("ReactiveStreamsUnusedPublisher")
            verify(exactly = 1) {
                esportClient.getMatches(MatchType.RUNNING)
                esportClient.getMatches(MatchType.UPCOMING)
            }
        }
    }) {
    companion object {
        private fun verifyResult(matches: Mono<EsportVm>) {
            StepVerifier
                .create(matches)
                .expectNextCount(1)
                .verifyComplete()
        }
    }
}
