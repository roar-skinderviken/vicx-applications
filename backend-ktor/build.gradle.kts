
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "no.vicx"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth.jwt)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.cors)

    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.postgresql)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgres)
    implementation(libs.hikaricp)
    implementation(libs.exposed.core)
    implementation(libs.exposed.datetime)

    implementation(libs.graphql.kotlin.server)
    implementation(libs.graphql.kotlin.schema.generator)

    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)

    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")

    testImplementation(libs.ktor.server.test.host)
    implementation(libs.embedded.postgres) // TODO

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}