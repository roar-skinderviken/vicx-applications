package no.vicx.ktor

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.di.dependencies
import no.vicx.ktor.calculator.CalculatorService
import no.vicx.ktor.db.repository.CalculatorRepository
import no.vicx.ktor.db.repository.UserImageRepository
import no.vicx.ktor.db.repository.UserRepository
import no.vicx.ktor.esport.EsportClient
import no.vicx.ktor.esport.EsportService
import no.vicx.ktor.plugins.configureGraphQL
import no.vicx.ktor.plugins.configureHealth
import no.vicx.ktor.plugins.configureOldCalcEntryCleanup
import no.vicx.ktor.plugins.configureRestApi
import no.vicx.ktor.plugins.configureSecurity
import no.vicx.ktor.plugins.configureStatusPage
import no.vicx.ktor.plugins.connectToPostgres
import no.vicx.ktor.user.service.RecaptchaClient
import no.vicx.ktor.user.service.UserImageService
import no.vicx.ktor.user.service.UserService
import no.vicx.ktor.util.HttpClientConfig.defaultClient
import javax.sql.DataSource

// inline fun <reified T : Any> loggerFor(): Logger = LoggerFactory.getLogger(T::class.java)

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

suspend fun Application.module() {
    val esportToken = environment.config.property("esport.token").getString()
    val reCaptchaSecret = environment.config.property("recaptcha.secret").getString()
    val useEmbeddedPg =
        environment.config
            .property("postgres.embedded")
            .getString()
            .toBoolean()

    dependencies {
        provide { CalculatorRepository() }
        provide { UserRepository() }
        provide { UserImageRepository() }
        provide { defaultClient }

        provide { CalculatorService(resolve()) }
        provide { EsportClient(resolve(), esportToken) }
        provide { EsportService(resolve()) }
        provide { RecaptchaClient(resolve(), reCaptchaSecret) }
        provide { UserService(resolve(), resolve()) }
        provide { UserImageService(resolve(), resolve(), resolve()) }
        provide<DataSource> { this@module.connectToPostgres(useEmbeddedPg) }
    }

    // for localhost testing
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }

    configureHealth()
    configureSecurity()
    configureStatusPage()
    configureGraphQL()
    configureRestApi()
    configureOldCalcEntryCleanup()
}
