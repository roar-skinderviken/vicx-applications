dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // logging
    //runtimeOnly(libs.logback.access.spring.boot.starter)
    runtimeOnly(libs.logstash.logback.encoder)

    // database
    implementation(project(":database"))
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    implementation(libs.jackson.module.kotlin)

    // test
    testImplementation("org.htmlunit:htmlunit")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.extensions.spring)
    testImplementation(libs.mockk)
    testImplementation(libs.springmockk)
}

springBoot {
    mainClass = "no.vicx.authserver.AuthServerApplicationKt"
}

tasks.jar {
    enabled = false
}

tasks.test {
    jvmArgs(
        "-Xshare:off",
        "-XX:+EnableDynamicAgentLoading",
        "-Dkotest.framework.classpath.scanning.autoscan.disable=true",
        "-Dkotest.framework.config.fqn=no.vicx.authserver.KotestConfig"
    )
    useJUnitPlatform()
}
