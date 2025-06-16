package no.vicx.backend.calculator

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.mockk.called
import io.mockk.every
import io.mockk.verify
import no.vicx.backend.calculator.vm.CalcVm
import no.vicx.backend.SecurityTestUtils.AUTH_HEADER_IN_TEST
import no.vicx.backend.SecurityTestUtils.createPrincipalInTest
import no.vicx.database.calculator.CalculatorOperation
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime


@SpringBootTest
@AutoConfigureMockMvc
class CalculatorGraphQLControllerSpringBootTest(
    mockMvc: MockMvc,
    @MockkBean(relaxed = true) private val calculatorSecurityService: CalculatorSecurityService,
    @MockkBean(relaxed = true) private val calculatorService: CalculatorService,
    @MockkBean private val opaqueTokenIntrospector: OpaqueTokenIntrospector
) : BehaviorSpec({
    beforeContainer {
        every { opaqueTokenIntrospector.introspect(any()) } returns createPrincipalInTest(listOf("ROLE_USER"))
    }

    Given("deleteCalculations with mocked environment") {

        When("deleteCalculations without bearer") {
            val resultActions = mockMvc.perform(buildRequest(VALID_DELETE_BODY_IN_TEST,false))

            Then("expect Unauthorized") {
                resultActions
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.errors[0].message").value("Unauthorized"))

                verify { calculatorSecurityService wasNot called }
                verify(exactly = 0) { calculatorService.deleteByIds(any()) }
            }
        }

        When("deleteCalculations with ids for other user") {
            every { calculatorSecurityService.isAllowedToDelete(any(), any()) } returns false

            val resultActions = mockMvc.perform(buildRequest(VALID_DELETE_BODY_IN_TEST,true))

            Then("expect Forbidden") {
                resultActions
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.errors[0].message").value("Forbidden"))

                verify { calculatorSecurityService.isAllowedToDelete(listOf(1L, 2L, 3L), any()) }
                verify(exactly = 0) { calculatorService.deleteByIds(any()) }
            }
        }

        When("deleteCalculations with ids for current user") {
            every { calculatorSecurityService.isAllowedToDelete(any(), any()) } returns true

            val resultActions = mockMvc.perform(buildRequest(VALID_DELETE_BODY_IN_TEST,true))

            Then("expect OK") {
                resultActions
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.data.deleteCalculations").value(true))

                verify { calculatorService.deleteByIds(any()) }
            }
        }
    }

    Given("createCalculation") {
        forAll(
            row("authenticated user", true, "user1"),
            row("anonymous user", false, null)
        ) { description, useAuthenticatedUser, expectedUsername ->

            When("createCalculation, $description") {
                every { calculatorService.calculate(any(), any(), any(), any()) } returns expectedResponse(
                    useAuthenticatedUser
                )

                val resultActions = mockMvc.perform(buildRequest(VALID_CREATE_BODY_IN_TEST,useAuthenticatedUser))

                Then("expect OK") {
                    resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.createCalculation.id").value(1))
                        .andExpect(jsonPath("$.data.createCalculation.firstValue").value(1))
                        .andExpect(jsonPath("$.data.createCalculation.secondValue").value(2))
                        .andExpect(jsonPath("$.data.createCalculation.operation").value("PLUS"))
                        .andExpect(jsonPath("$.data.createCalculation.result").value(3))
                        .andExpect(jsonPath("$.data.createCalculation.username").value(expectedUsername))
                }
            }
        }
    }
}) {
    companion object {
        fun buildRequest(
            body: String,
            addBearerToken: Boolean
        ): MockHttpServletRequestBuilder = post("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body).apply {
                if (addBearerToken)
                    header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST)
            }

        val VALID_DELETE_BODY_IN_TEST: String = """
            {"query": "mutation { deleteCalculations(ids: [1, 2, 3]) }"}
            """.trimIndent()

        val VALID_CREATE_BODY_IN_TEST: String = """
            {"query": "mutation { createCalculation(firstValue: 1, secondValue: 2, operation: PLUS) {id firstValue secondValue operation result username createdAt} }"}
            """.trimIndent()

        fun expectedResponse(addBearerToken: Boolean) = CalcVm(
            id = 1,
            firstValue = 1,
            secondValue = 2,
            operation = CalculatorOperation.PLUS,
            result = 3,
            username = if (addBearerToken) "user1" else null,
            createdAt = LocalDateTime.now()
        )
    }
}