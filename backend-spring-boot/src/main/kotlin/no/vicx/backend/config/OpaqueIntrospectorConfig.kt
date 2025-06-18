package no.vicx.backend.config

import no.vicx.backend.jwt.github.GitHubFromOpaqueProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector


@Configuration(proxyBeanMethods = false)
class OpaqueIntrospectorConfig {

    @Bean
    fun opaqueTokenIntrospector(fromOpaqueProducer: GitHubFromOpaqueProducer) =
        OpaqueTokenIntrospector { token -> fromOpaqueProducer.createPrincipal(token) }
}