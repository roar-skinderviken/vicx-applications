package no.vicx.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.vicx.backend.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static no.vicx.backend.testconfiguration.SecurityTestUtils.createPrincipalInTest;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Import(SecurityConfig.class)
public abstract class BaseWebMvcTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected OpaqueTokenIntrospector opaqueTokenIntrospector;

    @BeforeEach
    void setUp() {
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(
                createPrincipalInTest(Collections.singletonList("ROLE_USER")));
    }
}
