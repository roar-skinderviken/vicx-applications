package no.vicx.backend;

import no.vicx.backend.testconfiguration.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
class ActuatorHealthWebTestClientTest {

    @LocalManagementPort
    private int managementPort;

    @Autowired
    WebTestClient webTestClient;

    @ParameterizedTest
    @ValueSource(strings = {"/liveness", "/readiness"})
    @DisplayName("Health Endpoint Probes Should Return HTTP 200")
    void testHealthProbes(String probePath) {
        webTestClient.get()
                .uri("http://localhost:%d/actuator/health%s".formatted(managementPort, probePath))
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{\"status\":\"UP\"}");
    }
}