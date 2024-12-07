package no.vicx.backend.info;

import no.vicx.backend.testconfiguration.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class GitPropertiesControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getGitProperties() throws Exception {
        mockMvc.perform(get("/gitproperties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branch").exists())
                .andExpect(jsonPath("$.commitId").exists())
                .andExpect(jsonPath("$.shortCommitId").exists())
                .andExpect(jsonPath("$.commitTime").exists());
    }
}