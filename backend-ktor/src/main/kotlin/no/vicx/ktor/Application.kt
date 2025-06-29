package no.vicx.ktor

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import no.vicx.ktor.calculator.CalculatorService
import no.vicx.ktor.calculator.RemoveOldEntriesTask
import no.vicx.ktor.db.repository.CalculatorRepository
import no.vicx.ktor.db.repository.UserImageRepository
import no.vicx.ktor.db.repository.UserRepository
import no.vicx.ktor.esport.EsportClient
import no.vicx.ktor.esport.EsportService
import no.vicx.ktor.esport.HttpClientConfig
import no.vicx.ktor.plugins.configureGraphQL
import no.vicx.ktor.plugins.configureHealth
import no.vicx.ktor.plugins.configureRestApi
import no.vicx.ktor.plugins.configureSecurity
import no.vicx.ktor.plugins.configureStatusPage
import no.vicx.ktor.plugins.connectToPostgres
import no.vicx.ktor.user.service.RecaptchaClient
import no.vicx.ktor.user.service.UserImageService
import no.vicx.ktor.user.service.UserService
import org.koin.core.module.dsl.singleOf
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.koinModule
import org.koin.logger.slf4jLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.sql.DataSource

inline fun <reified T : Any> loggerFor(): Logger = LoggerFactory.getLogger(T::class.java)

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    val useEmbeddedPg = environment.config.property("postgres.embedded").getString() == "true"
    val esportToken = environment.config.property("esport.token").getString()
    val reCaptchaSecret = environment.config.property("recaptcha.secret").getString()

    install(Koin) {
        slf4jLogger()
    }

    koinModule {
        singleOf(::CalculatorRepository)
        singleOf(::UserRepository)
        singleOf(::UserImageRepository)

        single { CalculatorService(get()) }
        single { EsportClient(HttpClientConfig.defaultClient, esportToken) }
        single { EsportService(get()) }
        single { RecaptchaClient(HttpClientConfig.defaultClient, reCaptchaSecret) }
        single { UserService(get(), get()) }
        single { UserImageService(get(), get(), get()) }
    }

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
    configureGraphQL(get(), get())
    configureRestApi(
        get(),
        get(),
        get(),
    )

    RemoveOldEntriesTask(get()).also { it.start(this) }
}
