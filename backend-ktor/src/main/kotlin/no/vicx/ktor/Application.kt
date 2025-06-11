package no.vicx.ktor

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import no.vicx.ktor.calculator.CalculatorService
import no.vicx.ktor.calculator.RemoveOldEntriesTask
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
import javax.sql.DataSource

inline fun <reified T : Any> loggerFor(): Logger = LoggerFactory.getLogger(T::class.java)

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val useEmbeddedPg = environment.config.property("postgres.embedded").getString() == "true"
    val esportToken = environment.config.property("esport.token").getString()
    val reCaptchaSecret = environment.config.property("recaptcha.secret").getString()

    val calculatorRepository = CalculatorRepository()
    val calculatorService = CalculatorService(CalculatorRepository())

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

    val dataSource: DataSource = connectToPostgres(useEmbeddedPg)
    configureHealth(dataSource)
    configureSecurity()
    configureStatusPage()
    configureGraphQL(calculatorService, calculatorRepository)
    configureRestApi(
        esportService,
        userService,
        userImageService
    )

    RemoveOldEntriesTask(calculatorRepository).also { it.start(this) }
}

