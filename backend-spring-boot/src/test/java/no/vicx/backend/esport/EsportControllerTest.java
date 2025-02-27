package no.vicx.backend.esport;

import no.vicx.backend.esport.vm.EsportMatchVm;
import no.vicx.backend.esport.vm.EsportVm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@WebFluxTest(EsportController.class)
class EsportControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    EsportService esportService;

    @TestConfiguration
    @EnableWebFluxSecurity
    static class EsportControllerTestConfiguration {
        @Bean
        public SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) {
            return http
                    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                    .build();
        }
    }

    @Test
    void getMatches_expectResult() {
        var expectedResult = new EsportVm(
                Collections.singletonList(createMatch("running")),
                Collections.singletonList(createMatch("upcoming"))
        );

        doReturn(Mono.just(expectedResult)).when(esportService).getMatches();

        webTestClient.get()
                .uri("/api/esport")
                .exchange()
                .expectStatus().isOk()
                .expectBody(EsportVm.class).isEqualTo(expectedResult);

        verify(esportService).getMatches();
    }

    private static EsportMatchVm createMatch(String status) {
        return new EsportMatchVm(
                42L,
                "Team 1 vs Team 2",
                "01/01/2024",
                status);
    }
}