package no.vicx.backend.calculator

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.vicx.backend.SecurityTestUtils
import no.vicx.database.calculator.CalculatorRepository
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class CalculatorSecurityServiceTest : StringSpec({
    val calculatorRepository: CalculatorRepository = mockk()
    val sut = CalculatorSecurityService(calculatorRepository)

    "when calling isAllowedToDelete with empty list of ids then expect true" {
        val result = sut.isAllowedToDelete(
            emptyList(),
            TOKEN_IN_TEST
        )

        result shouldBe true

        verify { calculatorRepository wasNot called }
    }

    "when calling isAllowedToDelete with ids that belongs to another user then expect false" {
        every { calculatorRepository.findAllIdsByUsername(any()) } returns emptySet()

        val result = sut.isAllowedToDelete(listOf(1L, 2L), TOKEN_IN_TEST)

        result shouldBe false
    }

    "when calling isAllowedToDelete with ids that belongs to user then expect true" {
        every { calculatorRepository.findAllIdsByUsername(any()) } returns setOf(1L)

        val result = sut.isAllowedToDelete(listOf(1L), TOKEN_IN_TEST)

        result shouldBe true
    }
}) {
    companion object {
        val TOKEN_IN_TEST: Authentication = JwtAuthenticationToken(
            SecurityTestUtils.createJwtInTest(listOf("USER"))
        )
    }
}