package no.vicx

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import no.vicx.calculator.CalculatorService
import no.vicx.db.repository.CalculatorRepository
import no.vicx.db.repository.UserRepository
import no.vicx.esport.EsportClient
import no.vicx.esport.EsportService
import no.vicx.esport.HttpClientConfig.defaultClient
import no.vicx.plugins.*
import no.vicx.user.service.RecaptchaClient
import no.vicx.user.service.UserImageService
import no.vicx.user.service.UserService
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
    val userService = UserService(recaptchaClient, userRepository)
    val userImageService = UserImageService()

    val esportService = EsportService(EsportClient(defaultClient, esportToken))

    //install(ContentNegotiation) { json() }

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
