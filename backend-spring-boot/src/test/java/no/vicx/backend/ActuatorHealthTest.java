package no.vicx.backend;

import no.vicx.backend.testconfiguration.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
class ActuatorHealthTest {

    @LocalManagementPort
    private int managementPort;

    @Autowired
    TestRestTemplate restTemplate;

    private String baseManagementUrl;

    @BeforeEach
    void setup() {
        baseManagementUrl = "http://localhost:" + managementPort + "/actuator/health";
    }

    @ParameterizedTest
    @ValueSource(strings = {"/liveness", "/readiness"})
    @DisplayName("Health Endpoint Probes Should Return HTTP 200")
    void testHealthProbes(String probePath) {
        var response = restTemplate.getForEntity(baseManagementUrl + probePath, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"status\":\"UP\"}", response.getBody());
    }
}