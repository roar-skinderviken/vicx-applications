plugins {
    id("vicx-library")
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(libs.ktor.server.di.jvm)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth.jwt)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.request.validation)

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

    implementation(libs.spring.security.crypto)
    implementation(libs.caffeine)
    implementation(libs.tika.core)
    implementation(libs.cohort.ktor)

    testImplementation(libs.ktor.server.test.host)
    implementation(libs.embedded.postgres) // TODO
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    mergeServiceFiles()
}

tasks.processResources {
    dependsOn("copyMigrations")
}

tasks.register<Copy>("copyMigrations") {
    from(
        project(":database")
            .layout.projectDirectory
            .dir("src/main/resources/db/migration"),
    )
    into(layout.buildDirectory.dir("resources/main/db.migration"))
}
