package no.vicx.backend

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringAutowireConstructorExtension
import io.kotest.extensions.spring.SpringExtension
import org.springframework.core.env.AbstractEnvironment

object KotestConfig : AbstractProjectConfig() {
    override suspend fun beforeProject() {
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "test")
    }

    override val extensions: List<Extension>
        get() = listOf(
            SpringExtension,
            SpringAutowireConstructorExtension,
        )
}
