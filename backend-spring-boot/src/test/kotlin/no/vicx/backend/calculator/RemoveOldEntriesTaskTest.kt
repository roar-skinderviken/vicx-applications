package no.vicx.backend.calculator

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.StringSpec
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import kotlin.time.Duration.Companion.seconds

@SpringBootTest
class RemoveOldEntriesTaskTest(
    @MockkBean(relaxed = true) private val calculatorService: CalculatorService
) : StringSpec({

    "expect remove old entries task to fire" {
        eventually(10.seconds) {
            verify(atLeast = 1) { calculatorService.deleteOldAnonymousCalculations() }
        }
    }
})