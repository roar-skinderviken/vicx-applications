package no.vicx.backend

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.slot
import no.vicx.backend.SecurityTestUtils.GITHUB_USER_TOKEN
import no.vicx.backend.SecurityTestUtils.VICX_USER_TOKEN
import no.vicx.backend.SecurityTestUtils.createPrincipalInTest
import no.vicx.backend.config.SecurityConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureWebTestClient
@Import(SecurityConfig::class)
abstract class BaseWebMvcTest(
    body: BaseWebMvcTest.() -> Unit,
) : BehaviorSpec() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var opaqueTokenIntrospector: OpaqueTokenIntrospector

    init {
        beforeContainer {
            clearAllMocks()

            val tokenSlot = slot<String>()
            every { opaqueTokenIntrospector.introspect(capture(tokenSlot)) } answers {
                val roles =
                    when (tokenSlot.captured) {
                        VICX_USER_TOKEN -> listOf("ROLE_USER")
                        GITHUB_USER_TOKEN -> listOf("ROLE_GITHUB_USER")
                        else -> emptyList()
                    }

                createPrincipalInTest(roles)
            }
        }

        body()
    }
}
