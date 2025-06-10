package no.vicx.ktor

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import no.vicx.ktor.calculator.CalculatorService
import no.vicx.ktor.db.repository.CalculatorRepository
import no.vicx.ktor.db.repository.UserImageRepository
import no.vicx.ktor.db.repository.UserRepository
import no.vicx.ktor.esport.EsportClient
import no.vicx.ktor.esport.EsportService
import no.vicx.ktor.esport.HttpClientConfig.defaultClient
import no.vicx.ktor.plugins.*
import no.vicx.ktor.user.service.RecaptchaClient
import no.vicx.ktor.user.service.UserImageService
import no.vicx.ktor.user.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration

inline fun <reified T : Any> loggerFor(): Logger = LoggerFactory.getLogger(T::class.java)

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val esportToken = environment.config.property("esport.token").getString()
    val reCaptchaSecret = environment.config.property("user.recaptcha.secret").getString()

    val calculatorRepository = CalculatorRepository()
    val calculatorService = CalculatorService(CalculatorRepository(), Duration.ofHours(1))

    val recaptchaClient = RecaptchaClient(defaultClient, reCaptchaSecret)

    val userRepository = UserRepository()
    val userImageRepository = UserImageRepository()
    val userService = UserService(recaptchaClient, userRepository)
    val userImageService = UserImageService(userService, userRepository, userImageRepository)

    val esportService = EsportService(EsportClient(defaultClient, esportToken))

    // for localhost testing
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }

    connectToPostgres(true)
    configureSecurity()

    configureStatusPage()
    configureGraphQL(calculatorService, calculatorRepository)
    configureRestApi(
        esportService,
        userService,
        userImageService
    )
}
