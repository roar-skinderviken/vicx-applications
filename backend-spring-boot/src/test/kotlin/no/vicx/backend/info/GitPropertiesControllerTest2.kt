package no.vicx.backend.info

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import no.vicx.backend.config.SecurityConfig
import org.springframework.boot.info.GitProperties
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Properties

@WebMvcTest(GitPropertiesController::class)
@Import(SecurityConfig::class)
class GitPropertiesControllerTest2(
    mockMvc: MockMvc,
    @MockkBean private val opaqueTokenIntrospector: OpaqueTokenIntrospector,
) : StringSpec({

    "when calling GET /gitproperties then expect result" {
        mockMvc.perform(get("/gitproperties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.branch").value("main"))
            .andExpect(jsonPath("$.commitId").value("1"))
            .andExpect(jsonPath("$.shortCommitId").value("1"))
            .andExpect(jsonPath("$.commitTime").value(commitTimeAsString))
    }
}) {
    @TestConfiguration
    class GitPropertiesControllerTestConfiguration {

        @Bean
        fun mockGitProperties(): GitProperties = GitProperties(
            Properties().apply {
                setProperty("branch", "main")
                setProperty("commit.id", "1")
                setProperty("commit.time", commitTimeAsString)
            })
    }

    companion object {
        private val commitTimeAsString = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
    }
}