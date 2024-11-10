package no.vicx.backend.calculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.CalculatorOperation;
import no.vicx.backend.calculator.vm.CalculatorRequestVm;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void get_expectListOfCalculations() throws Exception {
        var expectedResponse = List.of(new CalcVm(
                1L,
                1,
                2,
                CalculatorOperation.PLUS,
                3,
                "user1",
                NOW));

        given(calculatorService.getAllCalculations())
                .willReturn(expectedResponse);

        var requestBuilder =
                get("/api/calculator").accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstValue", is(1)))
                .andExpect(jsonPath("$[0].secondValue", is(2)))
                .andExpect(jsonPath("$[0].operation", is(CalculatorOperation.PLUS.toString())))
                .andExpect(jsonPath("$[0].result", is(3)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[0].createdAt", is(NOW.toString())));
    }

    @ParameterizedTest
    @MethodSource("provideValidTestParameters")
    void givenValidParameters_expectOkAndResult(
            int firstValue, int secondValue, CalculatorOperation operation, int result, String username
    ) throws Exception {
        var expectedResponse = new CalcVm(
                1L,
                firstValue,
                secondValue,
                operation,
                result,
                username,
                NOW);

        var requestBody = new CalculatorRequestVm(
                (long) firstValue,
                (long) secondValue,
                operation);

        var requestBuilder = post("/api/calculator")
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        if (username != null) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer token");
        }

        given(calculatorService.calculate(requestBody, username))
                .willReturn(expectedResponse);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstValue", is(firstValue)))
                .andExpect(jsonPath("$.secondValue", is(secondValue)))
                .andExpect(jsonPath("$.operation", is(operation.toString())))
                .andExpect(jsonPath("$.result", is(result)))
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.createdAt", is(NOW.toString())));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTestParameters")
    void givenInvalidParameters_expectBadRequest(
            Long firstValue, Long secondValue, CalculatorOperation operation) throws Exception {

        var requestBody = new CalculatorRequestVm(
                firstValue,
                secondValue,
                operation);

        var requestBuilder = post("/api/calculator")
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> provideValidTestParameters() {
        return Stream.of(
                Arguments.of(5, 10, CalculatorOperation.PLUS, 15, "user1"),
                Arguments.of(10, 5, CalculatorOperation.MINUS, 5, "user1"),
                Arguments.of(5, 10, CalculatorOperation.PLUS, 15, null)
        );
    }

    static Stream<Arguments> provideInvalidTestParameters() {
        return Stream.of(
                Arguments.of(null, 10L, CalculatorOperation.PLUS),
                Arguments.of(10L, null, CalculatorOperation.MINUS),
                Arguments.of(5L, 10L, null)
        );
    }

    static final ObjectMapper objectMapper = new ObjectMapper();
}
