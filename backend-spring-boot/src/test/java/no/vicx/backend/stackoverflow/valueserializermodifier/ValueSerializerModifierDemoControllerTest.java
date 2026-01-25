package no.vicx.backend.stackoverflow.valueserializermodifier;

import com.ninjasquad.springmockk.MockkBean;
import no.vicx.backend.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ValueSerializerModifierDemoController.class)
@Import({JsonMapperConfig.class, SecurityConfig.class})
public class ValueSerializerModifierDemoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @SuppressWarnings("unused")
    @MockkBean
    OpaqueTokenIntrospector opaqueTokenIntrospector;

    @Test
    void idShouldBeMasked() throws Exception {
        var resultActions = mockMvc.perform(get("/stackoverflow/get-base-models"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("usr_MQ=="))
                .andExpect(jsonPath("$[0].email").value("REDACTED"))
                .andExpect(jsonPath("$[1].id").value("usr_Mg=="))
                .andExpect(jsonPath("$[1].total").value(42.0));
    }
}
