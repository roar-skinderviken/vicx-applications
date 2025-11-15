package no.vicx.backend.config

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.KotlinModule

@Configuration(proxyBeanMethods = false)
class JsonCustomizerConfig {
    @Bean
    fun jackson3KotlinCustomizer(): JsonMapperBuilderCustomizer =
        JsonMapperBuilderCustomizer { builder ->
            builder.addModule(
                KotlinModule
                    .Builder()
                    .configure(KotlinFeature.NullIsSameAsDefault, true)
                    .build(),
            )
        }
}
