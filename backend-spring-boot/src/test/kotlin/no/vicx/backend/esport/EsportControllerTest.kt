package no.vicx.backend.esport

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import no.vicx.backend.esport.vm.EsportMatchVm
import no.vicx.backend.esport.vm.EsportVm
import no.vicx.backend.esport.vm.MatchType
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.Year

@WebFluxTest(EsportController::class)
class EsportControllerTest(
    webTestClient: WebTestClient,
    @MockkBean private val esportService: EsportService,
) : StringSpec({

        "when GET /api/esport when there are matches then expect matches in response" {
            val expectedResult =
                EsportVm(
                    listOf(createMatch(MatchType.RUNNING)),
                    listOf(createMatch(MatchType.UPCOMING)),
                )

            every { esportService.getMatches() } answers { Mono.just(expectedResult) }

            webTestClient
                .get()
                .uri("/api/esport")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(EsportVm::class.java)
                .isEqualTo(expectedResult)
        }
    }) {
    @TestConfiguration
    @EnableWebFluxSecurity
    class EsportControllerTestConfiguration {
        @Bean
        fun webFluxSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
            http
                .authorizeExchange { exchanges -> exchanges.anyExchange().permitAll() }
                .build()
    }

    companion object {
        private fun createMatch(status: MatchType): EsportMatchVm =
            EsportMatchVm(
                42L,
                "Team 1 vs Team 2",
                "01/01/${Year.now()}",
                status.name.lowercase(),
            )
    }
}
