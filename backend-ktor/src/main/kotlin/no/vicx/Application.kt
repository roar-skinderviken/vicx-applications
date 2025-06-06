package no.vicx

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import no.vicx.calculator.CalculatorService
import no.vicx.db.repository.CalculatorRepository
import no.vicx.esport.EsportClient
import no.vicx.esport.EsportService
import no.vicx.esport.HttpClientConfig.defaultClient
import no.vicx.plugins.configureSecurity
import no.vicx.plugins.configureSerialization
import no.vicx.plugins.connectToPostgres
import no.vicx.plugins.graphQLModule
import java.time.Duration

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val esportToken = environment.config.property("esport.token").getString()

    val calculatorService = CalculatorService(CalculatorRepository(), Duration.ofHours(1))
    val calculatorRepository = CalculatorRepository()
    val esportService = EsportService(EsportClient(defaultClient, esportToken))

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }

    connectToPostgres(true)
    configureSecurity()
    graphQLModule(calculatorService, calculatorRepository)
    configureSerialization(calculatorService, esportService)
}
