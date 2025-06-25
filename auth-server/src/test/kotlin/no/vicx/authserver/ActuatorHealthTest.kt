package no.vicx.authserver

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row1
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalManagementPort
import org.springframework.http.HttpStatus
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorHealthTest(
    @LocalManagementPort val managementPort: Int,
    restTemplate: TestRestTemplate,
) : BehaviorSpec({

        Given("an actuator") {

            forAll(
                Row1("liveness"),
                Row1("readiness"),
            ) { probeName ->

                When("calling getForEntity: $probeName") {
                    val uri =
                        UriComponentsBuilder
                            .fromUriString("http://localhost:{port}/actuator/health/{probeName}")
                            .buildAndExpand(managementPort, probeName)
                            .toUri()

                    val response = restTemplate.getForEntity(uri, String::class.java)

                    Then("Health endpoint probes should return OK") {
                        assertSoftly(response) {
                            statusCode shouldBe HttpStatus.OK
                            body shouldBe "{\"status\":\"UP\"}"
                        }
                    }
                }
            }
        }
    })
