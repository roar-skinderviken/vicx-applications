package no.vicx.authserver

import no.vicx.authserver.config.DefaultUserProperties
import no.vicx.authserver.config.OAuthProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

inline fun <reified T : Any> loggerFor(): Logger = LoggerFactory.getLogger(T::class.java)

@SpringBootApplication
@EnableConfigurationProperties(OAuthProperties::class, DefaultUserProperties::class)
class AuthServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(AuthServerApplication::class.java, *args)
}

