package no.vicx.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ActuatorHealthWebTestClientTest {

    @LocalManagementPort
    private int managementPort;

    @Autowired
    WebTestClient webTestClient;

    @ParameterizedTest
    @ValueSource(strings = {"liveness", "readiness"})
    @DisplayName("Health Endpoint Probes Should Return HTTP 200")
    void testHealthProbes(String probeName) {
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:{port}/actuator/health/{probeName}")
                .buildAndExpand(managementPort, probeName)
                .toUri();

        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{\"status\":\"UP\"}");
    }
}