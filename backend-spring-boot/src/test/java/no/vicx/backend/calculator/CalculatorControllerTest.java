package no.vicx.backend.calculator;

import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorOperation;
import no.vicx.backend.config.SecurityConfig;
import no.vicx.backend.testconfiguration.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class CalculatorControllerTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CalculatorService calculatorService;

    @ParameterizedTest
    @MethodSource("provideValidTestParameters")
    void givenValidParameters_expectOkAndResult(
            int firstValue, int secondValue, CalculatorOperation operation, int result, String username
    ) throws Exception {
        var expectedResponse = new CalcVm(
                firstValue,
                secondValue,
                operation,
                result,
                username,
                NOW,
                null);

        given(calculatorService.calculate(firstValue, secondValue, operation, username))
                .willReturn(expectedResponse);

        var requestBuilder =
                get(CalculatorController.URL_TEMPLATE, firstValue, secondValue, operation);

        if (username != null) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer token");
        }

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstValue", is(firstValue)))
                .andExpect(jsonPath("$.secondValue", is(secondValue)))
                .andExpect(jsonPath("$.operation", is(operation.toString())))
                .andExpect(jsonPath("$.result", is(result)))
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.createdAt", is(NOW.toString())));
    }

    @Test
    void givenInvalidOperation_expectBadRequest() throws Exception {
        var requestBuilder =
                get(CalculatorController.URL_TEMPLATE, 1, 2, "invalid");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideValidTestParameters() {
        return Stream.of(
                Arguments.of(5, 10, CalculatorOperation.PLUS, 15, "user1"),
                Arguments.of(10, 5, CalculatorOperation.MINUS, 5, "user1"),
                Arguments.of(5, 10, CalculatorOperation.PLUS, 15, null)
        );
    }
}
