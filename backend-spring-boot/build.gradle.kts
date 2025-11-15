plugins {
    id("vicx-library")
    alias(libs.plugins.git.properties)
}

dependencies {
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-jackson")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webclient")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-graphql")

    implementation("org.springframework.boot:spring-boot-restclient")

    implementation(libs.springdoc.openapi)

    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation(libs.tika.core)

    // logging
    // runtimeOnly(libs.logback.access.spring.boot.starter) TODO
    runtimeOnly(libs.logstash.logback.encoder)

    // database
    implementation(project(":database"))
    implementation("org.springframework.boot:spring-boot-flyway")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }

    testImplementation("org.springframework.graphql:spring-graphql-test")
    testImplementation("io.projectreactor:reactor-test")

    testImplementation("org.springframework.boot:spring-boot-webtestclient")
    testImplementation("org.springframework.boot:spring-boot-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-graphql-test")
    testImplementation("org.springframework.boot:spring-boot-webflux-test")
}

springBoot {
    mainClass = "no.vicx.backend.VicxBackendApplicationKt"
}

tasks.jar {
    enabled = false
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xannotation-default-target=param-property",
        )
    }
}

tasks.test {
    systemProperty("kotest.framework.config.fqn", "no.vicx.backend.KotestConfig")
}
