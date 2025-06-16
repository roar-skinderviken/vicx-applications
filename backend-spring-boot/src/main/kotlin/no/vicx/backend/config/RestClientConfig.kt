package no.vicx.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration(proxyBeanMethods = false)
class RestClientConfig {

    @Bean
    fun restClient(builder: RestClient.Builder): RestClient = builder.build()
}