package no.vicx.backend

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row2
import io.kotest.data.forAll
import io.kotest.matchers.collections.shouldContainAll
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.test.LocalManagementPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ActuatorHealthTest(
    @LocalManagementPort managementPort: Int,
    webTestClient: WebTestClient,
) : BehaviorSpec({

        Given("context with health checks") {
            forAll(
                Row2("readiness", setOf("db", "readinessState")),
                Row2("liveness", setOf("livenessState")),
            ) { probeName, expectedComponents ->
                val uri =
                    UriComponentsBuilder
                        .fromUriString("http://localhost:{port}/actuator/health/{probeName}")
                        .buildAndExpand(managementPort, probeName)
                        .toUri()

                Then("$probeName should have $expectedComponents") {
                    webTestClient
                        .get()
                        .uri(uri)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody<Map<String, Any>>()
                        .consumeWith { result ->
                            val actualComponents = result.responseBody?.get("components") as Map<*, *>
                            actualComponents.keys shouldContainAll expectedComponents
                        }
                }
            }
        }
    })
