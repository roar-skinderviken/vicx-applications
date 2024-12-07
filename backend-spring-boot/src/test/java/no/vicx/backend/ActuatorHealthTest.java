package no.vicx.backend;

import no.vicx.backend.testconfiguration.TestSecurityConfig;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
class ActuatorHealthTest {

    @LocalManagementPort
    private int managementPort;

    @Autowired
    WebTestClient webClient;

    @ParameterizedTest
    @MethodSource("probeEndpoints")
    void getHealthProbe_givenExpectedComponents_expectComponentsAsInConfig(
            String probeEndpoint, List<String> expectedComponents) {

        var currentProbeUrl = "http://localhost:%d/actuator/health%s"
                .formatted(managementPort, probeEndpoint);

        webClient.get()
                .uri(currentProbeUrl)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(result -> {
                    @SuppressWarnings({"unchecked", "DataFlowIssue"})
                    var actualComponents = (Map<String, Object>) result.getResponseBody().get("components");

                    assertEquals(expectedComponents.size(), actualComponents.size());
                    assertThat(actualComponents.keySet(), containsInAnyOrder(expectedComponents.toArray()));
                });
    }

    private static Stream<Arguments> probeEndpoints() {
        return Stream.of(
                Arguments.of("/readiness", List.of("db", "readinessState")),
                Arguments.of("/liveness", Collections.singletonList("livenessState"))
        );
    }
}