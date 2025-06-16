package no.vicx.backend.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.WebClient


@Configuration(proxyBeanMethods = false)
class WebClientConfig {

    @Bean
    fun webClient(
        builder: WebClient.Builder,
        objectMapper: ObjectMapper
    ): WebClient =
        builder
            .codecs { clientCodecConfigurer ->
                clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
                clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
            }
            .build()
}
