package no.vicx.backend.info

import io.kotest.core.spec.style.StringSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class GitPropertiesControllerIntegrationTest(
    mockMvc: MockMvc,
) : StringSpec({
        "when calling GET /gitproperties then expect result" {
            mockMvc
                .perform(get("/gitproperties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branch").exists())
                .andExpect(jsonPath("$.commitId").exists())
                .andExpect(jsonPath("$.shortCommitId").exists())
                .andExpect(jsonPath("$.commitTime").exists())
        }
    })
