plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    runtimeOnly("com.github.ben-manes.caffeine:caffeine")
    runtimeOnly("dev.akkinoc.spring.boot:logback-access-spring-boot-starter:4.4.0")

    implementation("org.apache.tika:tika-core:3.0.0")

    implementation(project(":database"))
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

springBoot {
    mainClass = "no.vicx.backend.VicxBackendApplication"
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
