package no.vicx.authserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorHealthTest {

    @LocalManagementPort
    private int managementPort;

    @Autowired
    TestRestTemplate restTemplate;

    @ParameterizedTest
    @ValueSource(strings = {"liveness", "readiness"})
    @DisplayName("Health Endpoint Probes Should Return HTTP 200")
    void testHealthProbes(String probeName) {
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:{port}/actuator/health/{probeName}")
                .buildAndExpand(managementPort, probeName)
                .toUri();

        var response = restTemplate.getForEntity(uri, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"status\":\"UP\"}", response.getBody());
    }
}