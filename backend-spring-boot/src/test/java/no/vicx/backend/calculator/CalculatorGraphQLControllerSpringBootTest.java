package no.vicx.backend.calculator;

import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.testconfiguration.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static no.vicx.backend.testconfiguration.TestSecurityConfig.AUTH_HEADER_IN_TEST;
import static no.vicx.database.calculator.CalculatorOperation.PLUS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class CalculatorGraphQLControllerSpringBootTest {

    final static String VALID_DELETE_BODY_IN_TEST = """
            {"query": "mutation { deleteCalculations(ids: [1, 2, 3]) }"}""";

    final static String VALID_CREATE_BODY_IN_TEST = """
            {"query": "mutation { createCalculation(firstValue: 1, secondValue: 2, operation: PLUS) {id firstValue secondValue operation result username createdAt} }"}""";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CalculatorSecurityService calculatorSecurityService;

    @MockitoBean
    CalculatorService calculatorService;

    @Test
    void deleteCalculations_givenNoAuthHeader_expectUnauthorized() throws Exception {
        var responseBody = mockMvc.perform(post("/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_DELETE_BODY_IN_TEST))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(
                """
                        {"errors":[{"message":"Unauthorized","locations":[{"line":1,"column":12}],"path":["deleteCalculations"],"extensions":{"classification":"UNAUTHORIZED"}}],"data":{"deleteCalculations":null}}""",
                responseBody
        );

        verify(calculatorSecurityService, never()).isAllowedToDelete(anyList(), any());
        verify(calculatorService, never()).deleteByIds(anyList());
    }

    @Test
    void deleteCalculations_givenIdsForOtherUser_expectForbidden() throws Exception {
        when(calculatorSecurityService.isAllowedToDelete(anyList(), any()))
                .thenReturn(false);

        var responseBody = mockMvc.perform(post("/graphql")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_DELETE_BODY_IN_TEST))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(
                """
                        {"errors":[{"message":"Forbidden","locations":[{"line":1,"column":12}],"path":["deleteCalculations"],"extensions":{"classification":"FORBIDDEN"}}],"data":{"deleteCalculations":null}}""",
                responseBody
        );

        verify(calculatorSecurityService).isAllowedToDelete(eq(List.of(1L, 2L, 3L)), any());
        verify(calculatorService, never()).deleteByIds(anyList());
    }

    @Test
    void deleteCalculations_givenIsAllowedToDelete_expectTrue() throws Exception {
        when(calculatorSecurityService.isAllowedToDelete(anyList(), any()))
                .thenReturn(true);

        var responseBody = mockMvc.perform(post("/graphql")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_DELETE_BODY_IN_TEST))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(
                """
                        {"data":{"deleteCalculations":true}}""",
                responseBody
        );

        verify(calculatorService).deleteByIds(List.of(1L, 2L, 3L));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void createCalculation_expectCreated(boolean isAuthenticated) throws Exception {
        var expectedUsername = isAuthenticated ? "user1" : null;
        var expected = new CalcVm(1L, 1L, 2L, PLUS, 3,
                expectedUsername, LocalDateTime.of(2024, 1, 1, 1, 1, 1));

        when(calculatorService.calculate(any(), any())).thenReturn(expected);

        var requestBuilder = post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CREATE_BODY_IN_TEST);

        if (isAuthenticated) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST);
        }

        var responseBody = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String wrappedUsername = isAuthenticated
                ? "\"user1\""
                : null;

        var expectedResponse = String.format("""
                        {"data":{"createCalculation":{"id":"1","firstValue":1,"secondValue":2,"operation":"PLUS","result":3,"username":%s,"createdAt":"2024-01-01T01:01:01"}}}""",
                wrappedUsername);

        assertEquals(
                expectedResponse,
                responseBody
        );
    }
}
