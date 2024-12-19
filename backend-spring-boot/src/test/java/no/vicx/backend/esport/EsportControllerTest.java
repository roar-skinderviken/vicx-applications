package no.vicx.backend.esport;

import no.vicx.backend.config.SecurityConfig;
import no.vicx.backend.testconfiguration.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EsportController.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class EsportControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    EsportService esportService;

    @Test
    void getMatches_expectResult() throws Exception {
        mockMvc.perform(get("/api/esport"))
                .andExpect(status().isOk());

        verify(esportService).getMatches();
    }
}