package no.vicx.backend.calculator;

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

import java.util.List;

import static no.vicx.backend.testconfiguration.TestSecurityConfig.AUTH_HEADER_IN_TEST;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class CalculatorControllerDeleteTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CalculatorSecurityService calculatorSecurityService;

    @MockitoBean
    CalculatorService calculatorService;

    private static final String VALID_CONTENT_IN_TEST = "[1]";

    @Test
    void delete_givenNoAuthHeader_expectUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/calculator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT_IN_TEST))
                .andExpect(status().isUnauthorized());

        verify(calculatorSecurityService, never()).isAllowedToDelete(anyList(), any());
        verify(calculatorService, never()).deleteByIds(anyList());
    }

    @Test
    void delete_givenIdsForOtherUser_expectForbidden() throws Exception {
        when(calculatorSecurityService.isAllowedToDelete(anyList(), any()))
                .thenReturn(false);

        mockMvc.perform(delete("/api/calculator")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT_IN_TEST))
                .andExpect(status().isForbidden());

        verify(calculatorSecurityService).isAllowedToDelete(anyList(), any());
        verify(calculatorService, never()).deleteByIds(anyList());
    }

    @Test
    void delete_givenIsAllowedToDelete_expectOK() throws Exception {
        when(calculatorSecurityService.isAllowedToDelete(anyList(), any()))
                .thenReturn(true);

        mockMvc.perform(delete("/api/calculator")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT_IN_TEST))
                .andExpect(status().isNoContent());

        verify(calculatorService).deleteByIds(List.of(1L));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "[]", "[,1]"})
    void delete_givenInvalidParameters_expectBadRequest(String content) throws Exception {
        when(calculatorSecurityService.isAllowedToDelete(anyList(), any()))
                .thenReturn(true);

        mockMvc.perform(delete("/api/calculator")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_IN_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());

        verify(calculatorService, never()).deleteByIds(anyList());
    }
}
