package no.vicx.backend.calculator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

import java.util.stream.Stream;

import no.vicx.backend.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

@WebMvcTest(ApiController.class)
@Import(SecurityConfig.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalculatorService calculatorService;

    @ParameterizedTest
    @MethodSource("provideValidTestParameters")
    void givenValidParameters_expectOkAndResult(
            long firstValue, long secondValue, CalculatorOperation operation, Long result
    ) throws Exception {
        var expectedResponse = new CalcVm(firstValue, secondValue, operation, result);

        given(calculatorService.calculate(firstValue, secondValue, operation))
                .willReturn(expectedResponse);

        var requestBuilder = get(ApiController.URL_TEMPLATE, firstValue, secondValue, operation).
                contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstValue", is((int) firstValue)))
                .andExpect(jsonPath("$.secondValue", is((int) secondValue)))
                .andExpect(jsonPath("$.operation", is(operation.toString())))
                .andExpect(jsonPath("$.result", is(result.intValue())));
    }

    @Test
    void givenInvalidOperation_expectBadRequest() throws Exception {
        var requestBuilder = get(ApiController.URL_TEMPLATE, 1, 2, "invalid").
                contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideValidTestParameters() {
        return Stream.of(
                Arguments.of(5L, 10L, CalculatorOperation.PLUS, 15L),
                Arguments.of(10L, 5L, CalculatorOperation.MINUS, 5L)
        );
    }
}
