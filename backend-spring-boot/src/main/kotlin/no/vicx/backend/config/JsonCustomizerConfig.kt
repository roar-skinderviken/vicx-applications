package no.vicx.backend.config

import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration(proxyBeanMethods = false)
class JsonCustomizerConfig {

    @Bean
    fun jsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer =
        Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->

            builder.modulesToInstall(
                KotlinModule.Builder() // will replace existing KotlinModule
                    // comment in features when required

                    // needed, else it will break for null https://github.com/FasterXML/jackson-module-kotlin/issues/130#issuecomment-546625625
                    .configure(KotlinFeature.NullIsSameAsDefault, true)
                    //.configure(KotlinFeature.NullToEmptyCollection, false)
                    //.configure(KotlinFeature.NullToEmptyMap, false)
                    //.configure(KotlinFeature.StrictNullChecks, false)
                    .build()
            )
        }
}