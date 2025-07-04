plugins {
    id("vicx-library")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // logging
    // runtimeOnly(libs.logback.access.spring.boot.starter)
    runtimeOnly(libs.logstash.logback.encoder)

    // database
    implementation(project(":database"))
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    // test
    testImplementation("org.htmlunit:htmlunit")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("org.springframework.security:spring-security-test")
}

springBoot {
    mainClass = "no.vicx.authserver.AuthServerApplicationKt"
}

tasks.jar {
    enabled = false
}

tasks.test {
    @Suppress("UNNECESSARY_SAFE_CALL")
    jvmArgs?.add("-Dkotest.framework.config.fqn=no.vicx.authserver.KotestConfig")
}
