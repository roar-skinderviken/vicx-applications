package no.vicx.backend.info;

import no.vicx.backend.BaseWebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GitPropertiesController.class)
class GitPropertiesControllerTest extends BaseWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    // alternative: Using @MockitoBean
    @TestConfiguration
    static class SecondGitPropertiesControllerTestConfiguration {

        @Bean
        GitProperties gitProperties() {
            return new GitProperties(new PropertiesBuilder()
                    .setProperty("branch", "master")
                    .setProperty("commit.id", "1")
                    .setProperty("commit.time", NOW_AS_STRING)
                    .build());
        }
    }

    @Test
    void getGitProperties() throws Exception {
        mockMvc.perform(get("/gitproperties"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.branch", is("master")))
                .andExpect(jsonPath("$.commitId", is("1")))
                .andExpect(jsonPath("$.shortCommitId", is("1")))
                .andExpect(jsonPath("$.commitTime", is(NOW_AS_STRING)));
    }

    private static final String NOW_AS_STRING =
            DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(ChronoUnit.SECONDS));

    static class PropertiesBuilder {
        private final Properties properties = new Properties();

        public PropertiesBuilder setProperty(String key, String value) {
            properties.setProperty(key, value);
            return this;
        }

        public Properties build() {
            return properties;
        }
    }
}