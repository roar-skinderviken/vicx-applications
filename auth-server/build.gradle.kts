plugins {
    java
    // remaining plugins are added in parent
}

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

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

    // test
    testImplementation("org.htmlunit:htmlunit")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

springBoot {
    mainClass = "no.vicx.authserver.AuthServerApplication"
}

tasks.jar {
    enabled = false
}

// https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#0.3
val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    testImplementation(libs.mockito)
    mockitoAgent(libs.mockito) { isTransitive = false }
}

tasks.test {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
}
